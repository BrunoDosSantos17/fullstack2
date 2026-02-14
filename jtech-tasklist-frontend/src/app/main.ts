import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPersist from 'pinia-plugin-persistedstate'
import 'vuetify/styles'
import App from '../App.vue'
import router from '../router/router.ts'
import vuetify from './vuetify'
import '../assets/navigation.css'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-icons/font/bootstrap-icons.css'

const pinia = createPinia()
pinia.use(piniaPersist)

createApp(App).use(pinia).use(router).use(vuetify).mount('#app')
