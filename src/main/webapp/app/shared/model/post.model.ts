import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IForumPost } from 'app/shared/model/forum-post.model';

export interface IPost {
  id?: number;
  title?: string;
  content?: string;
  created_at?: dayjs.Dayjs | null;
  media_url?: string | null;
  user?: IUser | null;
  forumPost?: IForumPost | null;
}

export const defaultValue: Readonly<IPost> = {};
