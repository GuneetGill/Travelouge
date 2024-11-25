package com.example.travelogue.table_doc
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import com.example.travelogue.folder_table.Folder

@Entity(
//    foreignKeys = [ForeignKey(
//        entity = Folder::class,
//        parentColumns = ["folderId"],
//        childColumns = ["folder_owner_id_col"],
//        onDelete = ForeignKey.CASCADE
//    )]
    tableName = "document_table",
    foreignKeys = [
        ForeignKey(
            entity = Folder::class,
            parentColumns = ["folder_id"],
            childColumns = ["folder_owner_id_col"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Document(
    @PrimaryKey(autoGenerate = true)
    var docId: Long = 0L,

    @ColumnInfo(name = "folder_owner_id_col")
    var folderOwnerId: Long = 0L,

    @ColumnInfo(name = "doc_name")
    var docName: String = "",

    @ColumnInfo(name = "doc_img_data")
    var docImgData: ByteArray = byteArrayOf(),

    @ColumnInfo
    val createdAt: Long = System.currentTimeMillis()
)