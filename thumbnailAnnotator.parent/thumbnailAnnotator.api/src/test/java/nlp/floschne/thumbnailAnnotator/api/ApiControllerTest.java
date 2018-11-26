package nlp.floschne.thumbnailAnnotator.api;

import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.UserInput;
import nlp.floschne.thumbnailAnnotator.db.RedisConfig;
import nlp.floschne.thumbnailAnnotator.db.repository.CaptionTokenEntityRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class, RedisConfig.class})
@WebAppConfiguration
public class ApiControllerTest {

    //TODO test all api endpoints!

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Autowired
    CaptionTokenEntityRepository repository;

    @Before
    public void setup() {
    }

    @Test
    public void helloWorldITest() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    // TODO test all fields of CaptionToken
    public void extractCaptionTokensITest() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String value = "The red, broken and big car control system is great.";

        mockMvc.perform(MockMvcRequestBuilders.post("/extractCaptionTokens/")
                .content(this.asJson(new UserInput(value)))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasKey("captionTokens")))
                .andExpect(jsonPath("$.captionTokens", hasSize(1)))
                .andExpect(jsonPath("$.captionTokens[0]", hasKey("value")))
                .andExpect(jsonPath("$.captionTokens[0].value", equalTo("red, broken and big car control system")))
                .andExpect(jsonPath("$.captionTokens[0]", hasKey("type")))
                .andExpect(jsonPath("$.captionTokens[0].type", equalToIgnoringCase(CaptionToken.Type.COMPOUND.toString())))
                .andExpect(jsonPath("$.captionTokens[0]", hasKey("posTags")))
                .andExpect(jsonPath("$.captionTokens[0].posTags", hasSize(8)))
                .andExpect(jsonPath("$.captionTokens[0]", hasKey("tokens")))
                .andExpect(jsonPath("$.captionTokens[0].tokens", hasSize(8)))
                .andExpect(jsonPath("$", hasKey("userInput")))
                .andExpect(jsonPath("$.userInput", hasKey("value")))
                .andExpect(jsonPath("$.userInput.value", equalTo(value)));
    }

    private String asJson(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
