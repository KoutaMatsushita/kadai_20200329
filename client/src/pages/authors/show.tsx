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
import { Author } from "../../@types/api";
import { LargeProgress } from "../../components/LargeProgress";
import { fetcher } from "../../util/fetcher";
import { NotFoundPage } from "../404";

const AuthorForm: React.FC<{
  author: Author;
  onSubmit: (author: Author) => any;
  onDelete: (author: Author) => any;
}> = ({ author, onSubmit, onDelete }) => {
  const [editable, setEditable] = useState(false);
  const [authorName, setAuthorName] = useControllableState({
    defaultValue: author.name,
  });
  const _onSubmit = useCallback(async () => {
    await onSubmit({ ...author, name: authorName });
    setEditable(false);
  }, [author, authorName, onSubmit]);

  const _onDelete = useCallback(async () => {
    onDelete(author);
  }, [author, onDelete]);

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
                disabled={!authorName}
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
                placeholder="input author name"
                value={authorName}
                onChange={(e) => setAuthorName(e.currentTarget.value)}
              />
            ) : (
              <Text>{author.name}</Text>
            )}
          </FormControl>
        </VStack>
      </form>
    </VStack>
  );
};

export const AuthorShowPage: React.FC = () => {
  const history = useHistory();
  const { id } = useParams<{ id: string }>();
  const { data: author, error } = useSWR<Author>(`/api/authors/${id}`, fetcher);

  const onSubmit = useCallback(
    async (author: Author) => {
      await ky.patch(`/api/authors/${id}`, {
        json: { name: author.name },
      });
      mutate(`/api/authors/${id}`);
    },
    [id]
  );

  const onDelete = useCallback(
    async (author: Author) => {
      if (!window.confirm("Are you sure?")) return;
      try {
        await ky.delete(`/api/authors/${id}`);
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
  if (!author) {
    return <LargeProgress />;
  }

  return <AuthorForm author={author} onSubmit={onSubmit} onDelete={onDelete} />;
};
