import { Crawler } from "@/features/crawler";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/app/crawler")({
  component: Crawler,
});
