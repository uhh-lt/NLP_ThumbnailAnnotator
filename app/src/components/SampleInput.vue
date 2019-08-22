<template>
  <div class="mt-5">
    <div class="card shadow-sm">
      <div class="card-header card-title text-md-center h5">Example Input</div>
      <div class="card-body">
        <code v-if="siddhartha">
          Siddhartha learned something new on every step of his path, for the world was transformed, and his heart was enchanted. He saw the sun rising over the mountains with their forests
          and
          setting over the distant beach with its palm-trees. At night, he saw the stars in the sky in their fixed positions and the crescent of the moon floating like a boat in the blue. He
          saw
          trees, stars, animals, clouds, rainbows, rocks, herbs, flowers, stream and river, the glistening dew in the bushes in the morning, distant hight mountains which were blue and pale,
          birds
          sang and bees, wind silverishly blew through the rice-field. All of this, a thousand-fold and colourful, had always been there, always the sun and the moon had shone, always rivers
          had
          roared and bees had buzzed, but in former times all of this had been nothing more to Siddhartha than a fleeting, deceptive veil before his eyes, looked upon in distrust, destined to
          be
          penetrated and destroyed by thought, since it was not the essential existence, since this essence lay beyond, on the other side of, the visible.
        </code>
        <code v-else>
          <ul>
            <li>I like eating squash.</li>
            <li>I like to cook vegan squash dishes.</li>
            <li>Pumpkins are delicious!</li>
            <li>I like playing squash.</li>
            <li>Playing squash is very bad for your knees.</li>
            <li>I play squash for fun.</li>
            <li>The Jaguar is a dangerous animal.</li>
            <li>Jaguar is the name of a car brand.</li>
          </ul>
        </code>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: "SampleInput",
    props: {
      siddhartha: {
        required: true,
        type: Boolean,
        default: false
      }
    },
  }
</script>

<style scoped>

</style>
