import {StackDivider, Text, VStack} from "@chakra-ui/react"
import React, {useCallback} from "react";
import {useHistory} from "react-router";
import useSWR from "swr";
import {fetcher} from "util/fetcher";
import {Book} from "../@types/api";
import {LargeProgress} from "../components/LargeProgress";

const BookItem: React.FC<{ book: Book, onClick: (book: Book) => void }> = ({book, onClick}) => {
  const _onClick = useCallback(() => {
    onClick(book)
  }, [book.id])

  return (
    <VStack
      align="stretch"
      padding={2}
      _hover={{
        color: "teal.500",
        cursor: "pointer"
      }}
      role="group"
      onClick={_onClick}>
      <Text>{book.name}</Text>
      <Text fontSize="sm" color="gray.400" _groupHover={{color: "teal.400"}}>written by {book.author.name}</Text>
    </VStack>
  )
}

export const IndexPage: React.FC = () => {
  const {data} = useSWR<Book[]>("/api/books", fetcher)
  const history = useHistory()
  const onClick = useCallback((book: Book) => {
    history.push(`/books/${book.id}`)
  }, [])

  if (!data) {
    return <LargeProgress/>
  }

  return (
    <VStack align="stretch" divider={<StackDivider borderColor="gray.100"/>}>
      {data?.map(book => <BookItem key={book.id} book={book} onClick={onClick}/>)}
    </VStack>
  )
}