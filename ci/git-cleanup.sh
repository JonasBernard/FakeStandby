#!/bin/bash
if test "$(diff -w CONTRIBUTORS.md CONTRIBUTORS.old.md)"; then echo "CONTRIBUTORS.md will be updated."; else git restore CONTRIBUTORS.md; fi