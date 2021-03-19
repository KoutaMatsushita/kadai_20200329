import {
  FormControl,
  FormLabel,
  Input,
  useControllableState,
} from "@chakra-ui/react";
import ky from "ky";
import React, { useCallback } from "react";
import { useHistory } from "react-router";
import { AddButton } from "../../components/AddButton";

export const AuthorNewPage: React.FC = () => {
  const history = useHistory();
  const [authorName, setAuthorName] = useControllableState({
    defaultValue: "",
  });
  const onSubmit = useCallback(async () => {
    await ky.post("/api/authors", { json: { name: authorName } });
    history.push("/");
  }, [authorName, history]);

  return (
    <>
      <AddButton onClick={onSubmit} disabled={!authorName} />
      <form
        onSubmit={(e) => {
          e.preventDefault();
          onSubmit();
        }}
      >
        <FormControl>
          <FormLabel>name</FormLabel>
          <Input
            type="text"
            isRequired
            placeholder="input author name"
            value={authorName}
            onChange={(e) => setAuthorName(e.target.value)}
          />
        </FormControl>
      </form>
    </>
  );
};
