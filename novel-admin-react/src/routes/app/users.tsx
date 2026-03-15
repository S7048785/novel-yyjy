import { Users } from "@/features/user";
import { createFileRoute } from "@tanstack/react-router";
export const Route = createFileRoute("/app/users")({
  component: Users,
});
