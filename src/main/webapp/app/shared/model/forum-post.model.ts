import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';

export interface IForumPost {
  id?: number;
  name?: string;
  description?: string | null;
  created_at?: dayjs.Dayjs | null;
  created_by?: string | null;
  user?: IUser | null;
}

export const defaultValue: Readonly<IForumPost> = {};
