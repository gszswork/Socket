-- File Name: Proj1.hs
-- Author: SHUZHI GONG <shuzhig@student.unimelb.edu.au> ID: 1047975
-- Summary: BattleShip Guess Game, Project 1 for Declarative Programming.

-- Game Decription: The game is somewhat akin to the game of Battleship™, but 
-- somewhat simplified. The game is played on a 4×8 grid, and involves one 
-- player, the searcher trying to find the locations of three battleships hidden
-- by the other player, the hider. The searcher continues to guess until they 
-- find all the hidden ships. Unlike Battleship™, a guess consists of three 
-- different locations, and the game continues until the exact locations of the 
-- three hidden ships are guessed in a single guess. After each guess, the hider
-- responds with three numbers:
-- 1. the number of ships exactly located;
-- 2. the number of guesses that were exactly one space away from a ship; and
-- 3. the number of guesses that were exactly two spaces away from a ship.

-- In the file:
-- "Location" type decribes single position of a battleship;
-- "toLocation" function converts a string to a Location type; 
-- "feedback" return the responding 3 numbers of one guess according to the 
--         target. 
-- "GameState" is used to generate the next guess and contains all possible 
--         targets. 
-- "initialGuess" returns the initial guess and initial GameState. 
-- "nextGuess" returns the possible best guess and new GameState according to 
--         last GameState, last guess and feedback for last guess.
module Proj1 (Location, toLocation, feedback,
              GameState, initialGuess, nextGuess) where
import Data.List

-- |Location: the location of one position defined by a String (ex: "A1","B2")
-- deriving from Eq and implemented with instance Show.
data Location = Loc String deriving (Eq)
instance Show Location where
    show (Loc x) = x

-- |GameState: list of three different location, including all the possible 
-- targets, ex: [[A1,B2,C3],[A1,B2,C4]..]
type GameState = [[Location]]

-- |Score: the three number responded from the hider.
type Score = (Int, Int, Int)

-- |toLocation: return Location by a string. If the string is not a valid 
-- location name, toLocation will return Nothing. The string has to contain 2
-- characters and the first is in "ABCDEFGH" and the last in "1234".
toLocation :: String -> Maybe Location
toLocation string 
    | elem (head string) charList && elem (last string) numList 
      && length string ==2 = Just (Loc string)  
    | otherwise            = Nothing
    where 
    charList = ['A','B','C','D','E','F','G','H']
    numList  = ['1','2','3','4']

-- |initialGuess: returns the initial Guess and GameState after that guess.
-- Here we set the initial guess as [A2,D3,H2] to cover a large area as much.
-- The initial GameState contains all subsequeces with length 3 of 32 possible 
-- locations, except the initial guess.
initialGuess :: ([Location], GameState)
initialGuess = (guess, gameState)
    where 
    guess = [Loc "A2", Loc "D3", Loc "H2"]
    allLocation = [Loc [a,b]| a<-"ABCDEFGH", b<-"1234"]
    allGameState = subsequencesOfSize 3 allLocation
    gameState = delete guess allGameState

-- |subsequencesOfSize: given Int n and a List xs, returns all the subsequences 
-- of xs in length n. It cost less than "subsequences" implemented by Haskell.
subsequencesOfSize :: Int -> [a] -> [[a]]
subsequencesOfSize n xs = let l = length xs
                          in if n>l then [] else subsequencesBySize xs !! (l-n)
    where
    subsequencesBySize [] = [[[]]]
    subsequencesBySize (x:xs) = let next = subsequencesBySize xs
                             in zipWith (++) ([]:next) (map (map (x:)) next ++ [[]])

-- |feedback: given a target and a guess, returns the feedback score. 
-- Process:
-- 1. compute and get the minimum distance between each guess location and 3 
--    target locations. Get the [d1,d2,d3] as 3 distances for 3 guess.
-- 2. compute (count) the occurrence times of 0, 1, 2 in the list [d1,d2,d3] and
--    return the Score.
feedback :: [Location] -> [Location] -> Score
feedback [t1,t2,t3] [g1,g2,g3] = (same, oneDis, twoDis)
    where 
    same   = length (intersect [t1,t2,t3] [g1,g2,g3]) 
    oneDis = length $ filter (== 1) [d1,d2,d3]
    twoDis = length $ filter (== 2) [d1,d2,d3]
    d1 = min (distance t1 g1) (min (distance t2 g1) (distance t3 g1))
    d2 = min (distance t1 g2) (min (distance t2 g2) (distance t3 g2))
    d3 = min (distance t1 g3) (min (distance t2 g3) (distance t3 g3))
    
-- |distance: return the Distance between two single Location. The distance is 
-- the larger one of the 2 char ASCII distance (defined as charDis and numDis). 
distance :: Location -> Location -> Int
distance (Loc [a,b]) (Loc [c,d]) = max (abs charDis) (abs numbDis)
    where 
    charDis = (fromEnum a) - (fromEnum c)
    numbDis = (fromEnum b) - (fromEnum d)
          
-- |nextGuess: generates the best next guess based on last guess, last GameState,
-- last feedback score. The best next guess means we want to guess out the 
-- target in less times as possible.
-- Process:
-- 1. First we have the feedback 'Score' of last guess. The 'Score' describes a 
--    distance between last guess and the target. We just need to guess the 
--    possible target in GameState with the same distance (feedback) as 'Score'.
--    So we set the 'state' as all possible targets with the same feedback.
-- 2. Then we generate the best 'newGuess' by function bestGuess, which is 
--    described later.
-- 3. At last we update (delete 'newGuess') and return the new GameState. 
nextGuess :: ([Location], GameState) -> Score -> ([Location], GameState)
nextGuess (lastGuess, lastState) score = (newGuess, newState)
    where
    state = [pos | pos <- lastState, feedback pos lastGuess == score]
    newGuess = bestGuess state
    newState = delete newGuess state

-- |bestGuess: given all the possible targets as GameState, returns the best 
-- guess that may need least step to guess out the right target. The strategy 
-- is as follows: We already get a list of possible targets from nextGuess. To
-- guess out the right target in least steps, we calculate average guess times
-- for each possible target (implemented in avgScore function and allFeedback 
-- function), then take the minimum average guess time possible target as the 
-- best guess.
-- Process:
-- 1. get all average guess times for each possible target in avgScores, stored 
--    in a list.
-- 2. return the guess from current GameState with the minimum guess time.
-- 3. Plus: If there exists more than one minimum guess time possible target, 
--    return the first one in the GameState list.
bestGuess :: GameState -> [Location]
bestGuess gameState = guess
    where 
    avgScores = [score | pos <- gameState, let gameState' = delete pos gameState
                                         , let score = avgScore $ allFeedback pos gameState']
    mini = minimum avgScores
    index = head (elemIndices mini avgScores)
    guess = gameState !! index

-- |allFeedback: given a guess 'x' (possible target) and a list of guess (possible) 
-- targets, returns all the feedbacks betwwen 'x' and each guess in that list.
-- allFeedback function was called in bestGuess with avgScore function to 
-- calculate the average guess times. This function is implemented in recursive.
allFeedback :: [Location] -> [[Location]] -> [Score]
allFeedback _ [] = []
allFeedback pos (x:xs) = (feedback pos x) : (allFeedback pos xs)

-- |avgScore: Given a list of Scores, return the average Score time. This 
-- function is called in bestGuess function with allFeedback function. The 
-- theory basis is from Problem description, Hint 6.
-- Process:
-- 1. Given a list of Scores, sort the list and group.
-- 2. Count the count number (length) of each group as 'num'.
-- 3. Calculate the formula SUM count^2/ T, T is the whole length of Score list.
avgScore :: [Score] -> Float
avgScore scores = sum [ (num * num)/len | e <- groupedScores, 
                                          let num = fromIntegral $ length e]
    where 
    groupedScores = group $ sort scores
    len = fromIntegral (length scores)



