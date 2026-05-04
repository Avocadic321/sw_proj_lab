# Manual Test Run Summary

Input files from the Section 8.2 documentation are under `test-inputs/`.
Actual outputs are under `test-outputs/`.

Run command used for each file:

```bash
sh mvnw -q exec:java -Dexec.args="<input-file> <output-file>"
```

## Compatibility Changes Applied

- Added documented command aliases: `SET_TURN`, `FLOW`, `PICKUP_PIPE`, `PICKUP_PUMP`, `INSERT_PUMP`, `REPAIR_PIPE`.
- Added documented ID aliases:
  - `PL1` -> `PLUMBER0`, `PL2` -> `PLUMBER1`
  - `SB1` -> `SABOTEUR0`, `SB2` -> `SABOTEUR1`
  - `CIS1` -> `CISTERN1`, `SP1` -> `SPRING1`
- Commands such as `MOVE`, `CONNECT`, `DISCONNECT`, `SET_DIRECTION`, `SABOTAGE_PIPE`, and repair commands now accept documentation-style explicit player arguments where possible.
- Fixed a recursive disconnect path in `ActiveElement.disconnect()`.
- Initial players are now registered as occupants of their spawn element.

## Current Result

All 25 documented test inputs run to completion.

The earlier parser/id errors are fixed: there are no remaining `Unknown command`, `OBJECT_NOT_FOUND`, or documentation-ID lookup failures in the regenerated outputs.

Remaining errors are either expected by the negative tests or caused by the current implementation using a larger/different map than the documentation.

## Remaining Notable Mismatches

- TC11 and TC14 still fail around moving from `PIPE2` to `CIS1`/`CISTERN1`. In the documentation, `PIPE2` connects directly to `CIS1`; in the current code, `PIPE2` is a free-ended pipe connected to `PUMP1`, while `CISTERN1` is connected through `PIPE5`.
- TC12 still fails when `PL1` tries to move onto `PIPE1` after `SB1` already occupies it. This follows the current pipe single-occupancy rule, but the documented expected output later shows `PIPE1` occupied by both `PL1` and `SB1`.
- `FLOW` now runs, but the water path and stored-water results do not match the documentation because the current initial map is not the simple documented `SP1 -> PIPE1 -> PUMP1 -> PIPE2 -> CIS1` network.

## Expected Error Tests Still Produce Errors

These are normal for the documented negative cases:

- TC05: invalid disconnected move
- TC06: occupied pipe
- TC18: repair non-broken pipe
- TC19: double sabotage
- TC20: connect already-connected pipe end
- TC21: insert pump without carrying one
