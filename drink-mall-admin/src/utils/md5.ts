import { md5 } from 'js-md5'

export function useMD5(str: string): string {
  return md5(str)
}
