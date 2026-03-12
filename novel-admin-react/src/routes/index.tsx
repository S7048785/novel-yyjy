import { createFileRoute, redirect } from "@tanstack/react-router";
import { useUserStore } from "../lib/userStore";

export const Route = createFileRoute("/")({
  beforeLoad: async () => {
    const isAuthenticated = useUserStore.getState().isAuthenticated;
    if (!isAuthenticated) {
      throw redirect({
        to: "/login",
      });
    }

    throw redirect({
      to: "/app/dashboard",
    });
  },
});
