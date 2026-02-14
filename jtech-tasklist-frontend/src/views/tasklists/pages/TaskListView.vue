<template>
  <v-container fluid class="task-container">
    <!-- HEADER -->
    <div class="task-page-header">
      <div>
        <h2 class="page-title">
          {{ hasSelectedList ? 'Tarefas' : 'Nenhuma Lista Selecionada' }}
        </h2>

        <span v-if="hasSelectedList" class="page-subtitle">
          {{ tasks.items.length }} tarefa(s)
        </span>
      </div>

      <div class="header-actions">
        <!-- NOVA LISTA -->
        <v-btn class="vue-outlined-btn" variant="outlined" @click="showCreateListDialog = true">
          <i class="bi bi-folder-plus mr-1"></i>
          Nova Lista
        </v-btn>

        <!-- NOVA TAREFA -->
        <v-btn
          class="vue-primary-btn"
          :disabled="!hasSelectedList"
          @click="showCreateDialog = true"
        >
          <i class="bi bi-plus-circle mr-1"></i>
          Nova Tarefa
        </v-btn>
      </div>
    </div>

    <!-- SE NÃO HOUVER LISTA -->
    <div v-if="!hasSelectedList" class="empty-state">
      <i class="bi bi-folder2-open"></i>
      <p>Selecione ou crie uma lista para começar.</p>
    </div>

    <!-- LISTA DE TASKS -->
    <v-row v-else class="mt-6">
      <v-col cols="12" md="6" v-for="t in tasks.items" :key="t.id">
        <v-card class="task-card">
          <div class="task-content">
            <!-- CHECK -->
            <v-btn icon variant="text" @click.stop="toggle(t)">
              <i
                class="bi"
                :class="t.completed ? 'bi-check-square-fill text-success' : 'bi-square'"
                style="font-size: 1.3rem"
              ></i>
            </v-btn>

            <!-- INFO -->
            <div class="task-info">
              <div class="task-title" :class="{ completed: t.completed }">
                {{ t.title }}
              </div>

              <div
                v-if="t.description"
                class="task-description"
                :class="{ completed: t.completed }"
              >
                {{ t.description }}
              </div>

              <div v-if="t.dueDate" class="task-due" :class="{ completed: t.completed }">
                <i class="bi bi-calendar-event mr-1"></i>
                {{ formatDate(t.dueDate) }}
              </div>
            </div>

            <!-- ACTIONS -->
            <div class="task-actions">
              <v-btn variant="text" size="small" @click="view(t)">
                <i class="bi bi-eye"></i>
              </v-btn>

              <v-btn variant="text" size="small" @click="edit(t)">
                <i class="bi bi-pencil"></i>
              </v-btn>

              <v-btn variant="text" size="small" color="error" @click="remove(t.id)">
                <i class="bi bi-trash"></i>
              </v-btn>
            </div>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <!-- DIALOG CRIAR LISTA -->
    <v-dialog v-model="showCreateListDialog" max-width="400">
      <v-card>
        <v-card-title class="text-h6"> Criar Nova Lista </v-card-title>

        <v-card-text>
          <v-text-field
            v-model="newListName"
            label="Nome da Lista"
            variant="outlined"
            autofocus
            @keyup.enter="createList"
          />
        </v-card-text>

        <v-card-actions class="justify-end">
          <v-btn variant="text" @click="showCreateListDialog = false"> Cancelar </v-btn>

          <v-btn class="vue-primary-btn" :disabled="!newListName.trim()" @click="createList">
            Criar
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- DIALOG NOVA TAREFA -->
    <v-dialog v-model="showCreateDialog" max-width="500">
      <v-card>
        <v-card-title class="text-h6"> Nova Tarefa </v-card-title>

        <v-card-text>
          <v-text-field
            v-model="newTask.title"
            label="Título"
            variant="outlined"
            class="mb-3"
            autofocus
          />

          <v-textarea
            v-model="newTask.description"
            label="Descrição"
            variant="outlined"
            class="mb-3"
            rows="3"
          />

          <v-text-field
            v-model="newTask.dueDate"
            label="Data de Vencimento"
            type="date"
            variant="outlined"
          />
        </v-card-text>

        <v-card-actions class="justify-end">
          <v-btn variant="text" @click="closeCreateDialog"> Cancelar </v-btn>

          <v-btn class="vue-primary-btn" :disabled="!newTask.title.trim()" @click="createTask">
            Criar
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="showDeleteTaskDialog" max-width="400px">
      <v-card>
        <v-card-title class="text-h6">Confirmar Exclusão</v-card-title>
        <v-card-text>Tem certeza que deseja excluir esta tarefa?</v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="showDeleteTaskDialog = false">Cancelar</v-btn>
          <v-btn color="error" @click="confirmDeleteTask">Excluir</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="showEditDialog" max-width="420px" min-width="260px">
      <v-card>
        <v-card-title>Editar Tarefa</v-card-title>
        <v-card-text>
          <v-text-field v-model="editTitle" label="Título" required />
          <v-textarea v-model="editDescription" label="Descrição" rows="3" />
          <v-text-field v-model="editDueDate" label="Data de Vencimento" type="date" />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="showEditDialog = false">Cancelar</v-btn>
          <v-btn color="primary" @click="saveEdit">Salvar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="showViewDialog" max-width="420px" min-width="260px">
      <v-card>
        <v-card-title>Detalhes da Tarefa</v-card-title>
        <v-card-text>
          <p><strong>Título:</strong> {{ selectedTask?.title }}</p>
          <p v-if="selectedTask?.description"><strong>Descrição:</strong> {{ selectedTask.description }}</p>
          <p v-if="selectedTask?.dueDate"><strong>Data de Vencimento:</strong> {{ formatDate(selectedTask.dueDate) }}</p>
          <p><strong>Status:</strong> {{ selectedTask?.completed ? 'Concluída' : 'Pendente' }}</p>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="showViewDialog = false">Fechar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

  </v-container>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { useTasksStore } from '@/stores/useTasksStore'
import { useTasklistsStore } from '@/stores/useTasklistsStore'
import { useUiStore } from '@/stores/useUiStore'
import type { Task } from '@/core/models/task'

/* =========================
   PROPS
========================= */
const props = defineProps<{
  selectedListId: string | null
}>()

/* =========================
   STORES
========================= */
const tasks = useTasksStore()
const lists = useTasklistsStore()
const ui = useUiStore()

/* =========================
   STATES
========================= */
const showCreateDialog = ref(false)
const showCreateListDialog = ref(false)
const newListName = ref('')

const newTask = ref({
  title: '',
  description: '',
  dueDate: '',
})

const selectedTask = ref<Task | null>(null)
const showViewDialog = ref(false)
const showEditDialog = ref(false)
const showDeleteTaskDialog = ref(false)

const editTitle = ref('')
const editDescription = ref('')
const editDueDate = ref('')

/* =========================
   LISTA SELECIONADA?
========================= */
const hasSelectedList = computed(() => !!props.selectedListId)

/* =========================
   LOAD DINÂMICO
========================= */
watch(
  () => props.selectedListId,
  async (newListId) => {
    if (!newListId) {
      tasks.items = []
      return
    }

    await tasks.fetch(newListId)
  },
  { immediate: true },
)

/* =========================
   CRIAR LISTA
========================= */
const createList = async () => {
  const name = newListName.value.trim()
  if (!name) return

  if (lists.items.some((l) => l.name.toLowerCase() === name.toLowerCase())) {
    ui.showSnackbar('Já existe uma lista com esse nome.', 'error')
    return
  }

  await lists.create(name)

  ui.showSnackbar('Lista criada com sucesso!', 'success')

  newListName.value = ''
  showCreateListDialog.value = false
}

/* =========================
   CRIAR TASK
========================= */
const createTask = async () => {
  if (!props.selectedListId) return

  await tasks.create(
    props.selectedListId,
    newTask.value.title.trim(),
    newTask.value.description.trim() || undefined,
    newTask.value.dueDate || undefined,
  )

  ui.showSnackbar('Tarefa criada com sucesso!', 'success')

  await tasks.fetch(props.selectedListId)

  closeCreateDialog()
}

const closeCreateDialog = () => {
  showCreateDialog.value = false

  newTask.value = {
    title: '',
    description: '',
    dueDate: '',
  }
}

/* =========================
   TASK ACTIONS
========================= */
const toggle = async (t: Task) => {
  await tasks.toggle(t.id, !t.completed)
}

const edit = (t: Task) => {
  selectedTask.value = t
  showEditDialog.value = true
}

const view = (task: Task) => {
  selectedTask.value = task
  showViewDialog.value = true
}

const remove = (id: string) => {
  console.log('Removendo tarefa com id:', id)
  selectedTask.value = tasks.items.find((t) => t.id === id) || null
  showDeleteTaskDialog.value = true
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('pt-BR')
}

const confirmDeleteTask = async () => {
  if (selectedTask.value) {
    await tasks.remove(selectedTask.value.id)
    ui.showSnackbar('Tarefa excluída com sucesso!', 'success')
    showDeleteTaskDialog.value = false
  }
}

const saveEdit = async () => {
  if (!editTitle.value.trim()) return
  const title = editTitle.value.trim()
  if (tasks.items.some(t => t.id !== selectedTask.value?.id && t.title.toLowerCase() === title.toLowerCase())) {
    ui.showSnackbar('Já existe uma tarefa com esse nome nesta lista.', 'error')
    return
  }
  await tasks.update({ ...(selectedTask.value as Task), title, description: editDescription.value, dueDate: editDueDate.value })
  ui.showSnackbar('Tarefa atualizada com sucesso!', 'success')
  showEditDialog.value = false
}
</script>

<style scoped>
@import './style.css';
</style>
