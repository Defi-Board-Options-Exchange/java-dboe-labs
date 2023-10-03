@Name('InstIdSnapshotter') select * from InstWin
@Name('AliasInstIdSnapshotter') select * from AliasInstIdWin
@Name('BasketBasisSnapshotter') select * from BasketBasisWin output snapshot at (*/1, *, *, *, *)