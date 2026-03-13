interface CrawTaskStatus {
  readonly taskId: string;
  readonly status: string;
  readonly progress: number;
  readonly novelId: string;
  readonly novelName: string;
  readonly message: string;
  readonly startTime: string;
  readonly currentChapter: number;
  readonly totalChapters: number;
}
export type { CrawTaskStatus };
