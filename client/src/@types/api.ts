export type Author = {
  readonly id: number
  readonly name: string
}

export type Book = {
  readonly id: number
  readonly name: string
  readonly author: Author
}