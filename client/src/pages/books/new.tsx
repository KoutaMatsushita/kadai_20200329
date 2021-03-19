import {
  FormControl,
  FormLabel,
  Input,
  useControllableState,
  VStack,
} from "@chakra-ui/react";
import ky from "ky";
import React, { useCallback, useState } from "react";
import { useHistory } from "react-router";
import { AddButton } from "../../components/AddButton";
import {
  AutoCompleteInput,
  AutoCompleteInputItem,
} from "../../components/AutoCompleteInput";
import { fetcher } from "../../util/fetcher";
import { useAutoCompleteAuthors } from "../../util/useAutoCompleteAuthors";

export const BookNewPage: React.FC = () => {
  const [bookName, setBookName] = useControllableState({ defaultValue: "" });
  const [authorId, setAuthorId] = useState<string | number | null>(null);
  const history = useHistory();
  const {
    authorName,
    setAuthorName,
    autoCompleteAuthors,
  } = useAutoCompleteAuthors({
    defaultValue: "",
    fetcher: fetcher,
  });
  const onSubmit = useCallback(async () => {
    await ky.post("/api/books", { json: { name: bookName, authorId } });
    history.push("/");
  }, [bookName, authorId, history]);
  const onSelectAuthor = useCallback(
    (item: AutoCompleteInputItem) => {
      setAuthorName(item.text);
      setAuthorId(item.id);
    },
    [setAuthorName, setAuthorId]
  );

  return (
    <>
      <AddButton onClick={onSubmit} disabled={!bookName || !authorId} />
      <form
        onSubmit={(e) => {
          e.preventDefault();
          onSubmit();
        }}
      >
        <VStack spacing={8}>
          <FormControl id="name">
            <FormLabel>name</FormLabel>
            <Input
              type="text"
              isRequired
              placeholder="input book name"
              value={bookName}
              onChange={(e) => setBookName(e.currentTarget.value)}
            />
          </FormControl>
          <FormControl id="author">
            <FormLabel>author</FormLabel>
            <AutoCompleteInput
              type="text"
              isRequired
              placeholder="input author name"
              value={authorName}
              autoCompleteItems={autoCompleteAuthors}
              onChange={(e) => {
                setAuthorName(e.target.value);
                setAuthorId(null);
              }}
              onSelectItem={onSelectAuthor}
            />
          </FormControl>
        </VStack>
      </form>
    </>
  );
};
