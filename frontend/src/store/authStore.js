import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export const useAuthStore = create(
  persist(
    (set) => ({
      token: null,
      user: null, // { userId, nickname, phone, role, avatarUrl, yuanbaoPoints, totalStudyMinutes, isFirstLogin, textbookId, gradeId }

      setAuth: (token, user) => set({ token, user }),

      updateUser: (partial) =>
        set((state) => ({
          user: state.user ? { ...state.user, ...partial } : partial,
        })),

      logout: () => set({ token: null, user: null }),
    }),
    {
      name: 'poetry-app-auth',
      partialize: (state) => ({ token: state.token, user: state.user }),
    }
  )
);
