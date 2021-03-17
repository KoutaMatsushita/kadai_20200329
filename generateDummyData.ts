import {faker} from "https://deno.land/x/deno_faker@v1.0.3/mod.ts"

faker.setLocale("ja")

function getRandomInt(max: number): number {
  return Math.floor(Math.random() * Math.floor(max))
}

function getAuthorNames(num: number): string[] {
  const names: string[] = [...new Set([...Array(num * 10)].map(() => `${faker.name.lastName()} ${faker.name.firstName()}`))]
  if (names.length < num) {
    return getAuthorNames(num)
  } else {
    return names.slice(0, num)
  }
}

const authorValues = getAuthorNames(100).map((name) =>
  `('${name}')`
)
const bookValues = [...Array(500)].map(() =>
  `('${faker.name.title()}', ${getRandomInt(
    authorValues.length,
  ) + 1})`
)

const authorsQuery = `INSERT INTO authors (name) VALUES ${authorValues.join(",")};`
const booksQuery = `INSERT INTO books (name, author_id) VALUES ${bookValues.join(",")};`

console.log("TRUNCATE TABLE authors RESTART IDENTITY CASCADE;")
console.log("TRUNCATE TABLE books RESTART IDENTITY CASCADE;")

console.log("BEGIN;")
console.log(authorsQuery)
console.log(booksQuery)
console.log("COMMIT;")