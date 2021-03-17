import {Box, Container, Heading, Link, StackDivider, VStack} from "@chakra-ui/react"
import {IndexPage} from 'pages';
import React from 'react';
import {BrowserRouter as Router, Link as RouterLink, Route, Switch,} from "react-router-dom";
import {NotFoundPage} from "./pages/404";

function App() {
  return (
    <Router>
      <VStack
        divider={<StackDivider borderColor="gray.200"/>}
        spacing={4}
        padding={4}
        h="100%"
        align="stretch"
      >
        <Box>
          <Link>
            <RouterLink to="/">
              <Heading size="2xl">Syoseki</Heading>
            </RouterLink>
          </Link>
        </Box>
        <Container maxW="container.xl" h="100%">
          <Switch>
            <Route exact path="/" children={<IndexPage/>}/>
            <Route children={<NotFoundPage/>}/>
          </Switch>
        </Container>
      </VStack>
    </Router>
  );
}

export default App;
