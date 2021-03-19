import { CheckIcon, CloseIcon, DeleteIcon, EditIcon } from "@chakra-ui/icons";
import {
  Button,
  ButtonGroup,
  FormControl,
  FormLabel,
  Input,
  Text,
  useControllableState,
  VStack,
} from "@chakra-ui/react";
import ky from "ky";
import React, { useCallback, useState } from "react";
import { useHistory, useParams } from "react-router";
import useSWR, { mutate } from "swr";
import { Author, Book } from "../../@types/api";
import { AutoCompleteInput } from "../../components/AutoCompleteInput";
import { LargeProgress } from "../../components/LargeProgress";
import { fetcher } from "../../util/fetcher";
import { useAutoCompleteAuthors } from "../../util/useAutoCompleteAuthors";
import { NotFoundPage } from "../404";

const BookForm: React.FC<{
  book: Book;
  authors: Author[];
  onSubmit: (book: Book) => Promise<any>;
  onDelete: (book: Book) => any;
}> = ({ book, authors, onSubmit, onDelete }) => {
  const [editable, setEditable] = useState(false);
  const [bookName, setBookName] = useControllableState({
    defaultValue: book.name,
  });
  const [authorId, setAuthorId] = useState<string | number | null>(
    book.author.id
  );
  const {
    authorName,
    setAuthorName,
    autoCompleteAuthors,
  } = useAutoCompleteAuthors({
    defaultValue: book.author.name,
    fetcher: () => Promise.resolve(authors),
  });

  const _onSubmit = useCallback(async () => {
    await onSubmit({
      ...book,
      name: bookName,
      author: { ...book.author, id: Number(authorId) },
    });
    setEditable(false);
  }, [book, bookName, authorId, onSubmit]);

  const _onDelete = useCallback(async () => {
    onDelete(book);
  }, [book, onDelete]);

  return (
    <VStack spacing={8} align="stretch">
      <form
        onSubmit={(e) => {
          e.preventDefault();
          _onSubmit();
        }}
      >
        <ButtonGroup variant="outline">
          {editable ? (
            <>
              <Button
                leftIcon={<CheckIcon />}
                colorScheme="teal"
                onClick={() => _onSubmit()}
                disabled={!bookName || !authorId}
              >
                Save
              </Button>
              <Button
                leftIcon={<CloseIcon />}
                onClick={() => setEditable(false)}
              >
                Cancel
              </Button>
              <Button
                leftIcon={<DeleteIcon />}
                colorScheme="red"
                onClick={() => _onDelete()}
              >
                Delete
              </Button>
            </>
          ) : (
            <Button leftIcon={<EditIcon />} onClick={() => setEditable(true)}>
              Edit
            </Button>
          )}
        </ButtonGroup>
        <VStack spacing={8} align="stretch" pt={8}>
          <FormControl id="title">
            <FormLabel>Title</FormLabel>
            {editable ? (
              <Input
                type="text"
                isRequired
                placeholder="input book name"
                value={bookName}
                onChange={(e) => setBookName(e.currentTarget.value)}
              />
            ) : (
              <Text>{book.name}</Text>
            )}
          </FormControl>
          <FormControl id="author">
            <FormLabel>Author</FormLabel>
            {editable ? (
              <AutoCompleteInput
                type="text"
                isRequired
                placeholder="input author name"
                value={authorName}
                onChange={(e) => {
                  setAuthorName(e.target.value);
                  setAuthorId(null);
                }}
                autoCompleteItems={autoCompleteAuthors}
                onSelectItem={(item) => {
                  setAuthorName(item.text);
                  setAuthorId(item.id);
                }}
              />
            ) : (
              <Text>{book.author.name}</Text>
            )}
          </FormControl>
        </VStack>
      </form>
    </VStack>
  );
};

export const BookShowPage: React.FC = () => {
  const history = useHistory();
  const { id } = useParams<{ id: string }>();
  const { data: authors } = useSWR<Author[]>("/api/authors", fetcher);
  const { data: book, error } = useSWR<Book>(`/api/books/${id}`, fetcher);

  const onSubmit = useCallback(
    async (book: Book) => {
      await ky.patch(`/api/books/${id}`, {
        json: { name: book.name, authorId: book.author.id },
      });
      mutate(`/api/books/${id}`);
    },
    [id]
  );

  const onDelete = useCallback(
    async (book: Book) => {
      if (!window.confirm("Are you sure?")) return;
      try {
        await ky.delete(`/api/books/${id}`);
        alert("Delete success");
        history.replace("/");
      } catch (e) {
        console.error(e);
        alert(`Delete failure: ${e.message}`);
      }
    },
    [id, history]
  );

  if (error) {
    switch (error?.response?.status) {
      case 404:
        return <NotFoundPage />;
      default:
        return <NotFoundPage />; // TODO: ちゃんとやる
    }
  }
  if (!book || !authors) {
    return <LargeProgress />;
  }
  return (
    <BookForm
      book={book}
      authors={authors}
      onSubmit={onSubmit}
      onDelete={onDelete}
    />
  );
};
