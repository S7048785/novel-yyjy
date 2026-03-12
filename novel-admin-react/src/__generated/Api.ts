import type {Executor} from './';
import {
    AdminController, 
    BookController, 
    CaptchaController, 
    ChapterCommentController, 
    CommentController, 
    CrawlerController, 
    HomeController, 
    NewsController, 
    UserController
} from './services/';

export class Api {
    
    readonly adminController: AdminController
    
    readonly crawlerController: CrawlerController
    
    readonly bookController: BookController
    
    readonly captchaController: CaptchaController
    
    readonly chapterCommentController: ChapterCommentController
    
    readonly commentController: CommentController
    
    readonly homeController: HomeController
    
    readonly newsController: NewsController
    
    readonly userController: UserController
    
    constructor(executor: Executor) {
        this.adminController = new AdminController(executor);
        this.crawlerController = new CrawlerController(executor);
        this.bookController = new BookController(executor);
        this.captchaController = new CaptchaController(executor);
        this.chapterCommentController = new ChapterCommentController(executor);
        this.commentController = new CommentController(executor);
        this.homeController = new HomeController(executor);
        this.newsController = new NewsController(executor);
        this.userController = new UserController(executor);
    }
}