<template>
  <v-app class="app-container">
    <!-- Drawer -->
    <v-navigation-drawer v-if="isLogged" v-model="drawer" app permanent class="app-drawer">
      <v-list>
        <v-list-item class="drawer-title">
          <v-list-item-title> Minhas Listas </v-list-item-title>
        </v-list-item>

        <v-divider class="my-2" />

        <!-- Listas -->
        <v-list-item
          v-for="l in lists.items"
          :key="l.id"
          @click="selectList(l.id)"
          :class="['drawer-list-item', selectedListId === l.id ? 'active-list' : '']"
          link
        >
          <v-list-item-title>
            {{ l.name }}
          </v-list-item-title>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>

    <!-- APP BAR -->
    <v-app-bar app elevation="1" class="app-bar">
      <v-btn v-if="auth.isAuthenticated" @click="drawer = !drawer" icon>
        <i class="bi bi-list"></i>
      </v-btn>

      <v-toolbar-title class="app-title"> TaskList App </v-toolbar-title>

      <v-spacer />

      <!-- EDITAR LISTA -->
      <v-btn v-if="selectedList" icon variant="text" @click="openEditList">
        <i class="bi bi-pencil"></i>
      </v-btn>

      <!-- EXCLUIR LISTA -->
      <v-btn v-if="selectedList" icon variant="text" color="error" @click="showDeleteDialog = true">
        <i class="bi bi-trash"></i>
      </v-btn>

      <!-- LOGOUT -->
      <v-btn v-if="auth.isAuthenticated" @click="logout" variant="text">
        <i class="bi bi-box-arrow-right"></i>
        <span class="ml-1">Sair</span>
      </v-btn>
    </v-app-bar>

    <!-- MAIN -->
    <v-main>
      <router-view :selectedListId="selectedListId" />
    </v-main>

    <!-- SNACKBAR -->
    <v-snackbar v-model="ui.snackbar.show" :color="ui.snackbar.color" timeout="3000">
      {{ ui.snackbar.message }}
    </v-snackbar>

    <!-- ================= EDITAR LISTA ================= -->
    <v-dialog v-model="showEditDialog" max-width="400">
      <v-card>
        <v-card-title>Editar Lista</v-card-title>

        <v-card-text>
          <v-text-field v-model="editListName" label="Nome da Lista" variant="outlined" />
        </v-card-text>

        <v-card-actions class="justify-end">
          <v-btn variant="text" @click="showEditDialog = false"> Cancelar </v-btn>

          <v-btn class="vue-primary-btn" :disabled="!editListName.trim()" @click="updateList">
            Salvar
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- ================= EXCLUIR LISTA ================= -->
    <v-dialog v-model="showDeleteDialog" max-width="400">
      <v-card>
        <v-card-title class="text-error"> Excluir Lista </v-card-title>

        <v-card-text> Tem certeza que deseja excluir esta lista? </v-card-text>

        <v-card-actions class="justify-end">
          <v-btn variant="text" @click="showDeleteDialog = false"> Cancelar </v-btn>

          <v-btn color="error" variant="flat" @click="deleteList"> Excluir </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-app>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/useAuthStore'
import { useTasklistsStore } from '@/stores/useTasklistsStore'
import { useUiStore } from '@/stores/useUiStore'

const router = useRouter()
const auth = useAuthStore()
const lists = useTasklistsStore()
const ui = useUiStore()

const drawer = ref(false)
const selectedListId = ref<string | null>(null)

const showEditDialog = ref(false)
const showDeleteDialog = ref(false)
const editListName = ref('')

const isLogged = computed(() => auth.isAuthenticated)

const selectedList = computed(() =>
  lists.items.find(l => l.id === selectedListId.value)
)

onMounted(() => {
  if (!auth.isAuthenticated) {
    router.push({ name: 'login' })
  }
})

watch(
  () => auth.isAuthenticated,
  async (isAuth) => {
    if (isAuth) {
      await lists.fetch()
    } else {
      lists.items = []
      selectedListId.value = null
    }
  },
  { immediate: true }
)

const selectList = (listId: string) => {
  selectedListId.value = listId
  drawer.value = false
}

const openEditList = () => {
  if (!selectedList.value) return
  editListName.value = selectedList.value.name
  showEditDialog.value = true
}

const updateList = async () => {
  if (!selectedList.value) return

  await lists.rename(
    selectedList.value.id,
    editListName.value.trim()
  )

  ui.showSnackbar('Lista atualizada!', 'success')
  showEditDialog.value = false
}

const deleteList = async () => {
  try{
    if (!selectedList.value) return

      await lists.remove(selectedList.value.id)

      ui.showSnackbar('Lista excluída!', 'success')

      selectedListId.value = null
      showDeleteDialog.value = false
 }catch(err: any){
    const message = err.response?.data?.message || err.message || 'Erro ao excluir lista'
    ui.showSnackbar(message, 'error')
  }

}

const logout = () => {
  auth.logout()
  router.push({ name: 'login' })
}
</script>

<style scoped>
@import './styles/index.css';

/* Drawer Verde Vue */
</style>
