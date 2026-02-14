<template>
  <div class="login-wrapper">
    <v-card class="login-card" width="420">
      <v-card-title class="text-h6 text-center">
        Entrar
      </v-card-title>

      <v-card-text>
        <v-form @submit.prevent="onSubmit" ref="form">
          <v-text-field
            v-model="email"
            label="Email"
            :rules="[v => !!v || 'Email é obrigatório']"
            required
          />

          <v-text-field
            v-model="password"
            label="Senha"
            type="password"
            :rules="[v => !!v || 'Senha é obrigatória']"
            required
          />

          <v-btn
            type="submit"
            block
            class="mt-4 login-btn"
          >
            Entrar
          </v-btn>
        </v-form>

        <v-btn
          variant="text"
          block
          class="mt-3"
          @click="showRegister = true"
        >
          Cadastrar-se
        </v-btn>
      </v-card-text>
    </v-card>

    <!-- Dialog Cadastro -->
    <v-dialog v-model="showRegister" max-width="500px">
      <v-card>
        <v-card-title>Cadastrar Usuário</v-card-title>
        <v-card-text>
          <v-form @submit.prevent="onRegister" ref="registerForm">
            <v-text-field
              v-model="registerName"
              label="Nome"
              :rules="[v => !!v || 'Nome é obrigatório']"
              required
            />
            <v-text-field
              v-model="registerEmail"
              label="Email"
              :rules="[v => !!v || 'Email é obrigatório']"
              required
            />
            <v-text-field
              v-model="registerPassword"
              label="Senha"
              type="password"
              :rules="[v => !!v || 'Senha é obrigatória']"
              required
            />
            <v-btn type="submit" block class="mt-2">
              Cadastrar
            </v-btn>
          </v-form>
        </v-card-text>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '@/stores/useAuthStore'
import { useRouter } from 'vue-router'
import { useUiStore } from '@/stores/useUiStore'
import { Services } from '@/core/services'

const email = ref('demo@jtech.com')
const password = ref('123456')
const auth = useAuthStore()
const router = useRouter()
const ui = useUiStore()
const showRegister = ref(false)
const registerName = ref('')
const registerEmail = ref('')
const registerPassword = ref('')

const onSubmit = async () => {
  try {
    await auth.login(email.value, password.value)
    router.push({ name: 'lists' })
  } catch (err: any) {
    if (err.status === 403) {
      ui.showSnackbar('Usuário ou senha inválido', 'error')
    } else {
      ui.showSnackbar('Erro ao conectar com servidor', 'error')
    }
  }
}

const onRegister = async () => {
  try {
    await Services.auth.register({
      name: registerName.value,
      email: registerEmail.value,
      password: registerPassword.value,
    })
    ui.showSnackbar('Usuário cadastrado com sucesso!', 'success')
    showRegister.value = false
    registerName.value = ''
    registerEmail.value = ''
    registerPassword.value = ''
  } catch (err: any) {
    if (err.message.includes('Email inválido')) {
      ui.showSnackbar('Email inválido', 'error')
    } else if (err.message.includes('Já existe')) {
      ui.showSnackbar('Email já cadastrado', 'error')
    } else {
      ui.showSnackbar('Erro ao cadastrar usuário', 'error')
    }
  }
}
</script>

<style scoped>
  @import "./style.css";
</style>

