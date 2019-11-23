#include "mainwindow.h"
#include "ui_mainwindow.h"

#include <QFileDialog>
#include <QImage>
#include <QTimer>

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainWindow)
    , mSettings("FialasFiasco", "Die Gradient Combiner")
{
    ui->setupUi(this);

    ui->dieGraphicView->setScene(&mDieScene);
    mDieScene.addItem(&mDieImageItem);

    ui->gradientGraphicView->setScene(&mGradientScene);
    mGradientScene.addItem(&mGradientImageItem);

    ui->modifiedDieGraphicView->setScene(&mModifiedDieScene);
    mModifiedDieScene.addItem(&mModifiedDieImageItem);

    QObject::connect(ui->setDieDirectoryButton, &QPushButton::clicked, this, &MainWindow::chooseDieDirectory);
    QObject::connect(ui->setGradientButton, &QPushButton::clicked, this, &MainWindow::chooseGradient);
    QObject::connect(ui->combineButton, &QPushButton::clicked, this, &MainWindow::combine);

    auto oldDir = mSettings.value("dieDir", "").toString();
    setDieDirectory(oldDir);

    auto oldGradient = mSettings.value("gradientFile", "").toString();
    setGradient(oldGradient);

    QTimer::singleShot(0, this, [this]() {ui->dieGraphicView->fitInView(mDieScene.itemsBoundingRect(), Qt::KeepAspectRatio);});
    QTimer::singleShot(0, this, [this]() {ui->gradientGraphicView->fitInView(mGradientScene.itemsBoundingRect(), Qt::KeepAspectRatio);});

}

MainWindow::~MainWindow()
{
    mDieScene.removeItem(&mDieImageItem);
    mGradientScene.removeItem(&mGradientImageItem);
    mModifiedDieScene.removeItem(&mModifiedDieImageItem);
    delete ui;
}

void MainWindow::setDieDirectory(const QString& dirString)
{
    auto dir = QDir(dirString);

    if (!dirString.isEmpty() &&  dir.exists())
    {
        mSettings.setValue("dieDir", dirString);

        mDieImageFiles = dir.entryInfoList({"*.png"}, QDir::Files, QDir::Name);
        assert(mDieImageFiles.size() != 0);

        auto imageFile = mDieImageFiles[0];
        mDieImage.load(imageFile.absoluteFilePath());

        mDieImageItem.setPixmap(QPixmap::fromImage(mDieImage));
        ui->dieGraphicView->fitInView(mDieScene.itemsBoundingRect(), Qt::KeepAspectRatio);
    }
}

void MainWindow::chooseDieDirectory()
{
    auto dirString = QFileDialog::getExistingDirectory(this);

    setDieDirectory(dirString);
}

void MainWindow::setGradient(const QString& fileString)
{
    QFileInfo gradientFile(fileString);

    if (!fileString.isEmpty() && gradientFile.exists())
    {
        mGradientFile = gradientFile;

        mSettings.setValue("gradientFile", fileString);
        mGradientImage.load(gradientFile.absoluteFilePath());

        mGradientImageItem.setPixmap(QPixmap::fromImage(mGradientImage));
        ui->gradientGraphicView->fitInView(mGradientScene.itemsBoundingRect(), Qt::KeepAspectRatio);
    }
}

void MainWindow::chooseGradient()
{
    auto fileString = QFileDialog::getOpenFileName(this);

    setGradient(fileString);
}

void MainWindow::combine()
{
    if(mGradientImage.size() == QSize()) return;
    if(mDieImageFiles.size() == 0) return;

    auto newFileDir = mGradientFile.absoluteDir();
    newFileDir.cdUp();
    newFileDir.mkdir(mGradientFile.baseName());
    newFileDir.cd(mGradientFile.baseName());

    auto first = true;

    for(auto dieImageFile : mDieImageFiles)
    {
        QImage dieImage(dieImageFile.absoluteFilePath());
        auto dieGradientImage = applyGradient(dieImage);

        if(first)
        {
            first = false;
            mModifiedDieImage = dieGradientImage.copy();
            mModifiedDieImageItem.setPixmap(QPixmap::fromImage(mModifiedDieImage));
            ui->modifiedDieGraphicView->fitInView(mModifiedDieScene.itemsBoundingRect(), Qt::KeepAspectRatio);
        }

        auto newFileName = newFileDir.absoluteFilePath(dieImageFile.baseName() + "-" + mGradientFile.baseName() + ".png");

        dieGradientImage.save(newFileName);
    }
}


QImage MainWindow::applyGradient(const QImage& dieImage)
{
    auto dieGradientImage = dieImage.copy();

    auto imageRaw = dieGradientImage.bits();
    auto gradientRaw = mGradientImage.constBits();

    for(auto heightIndex = 0; heightIndex < dieGradientImage.height(); ++heightIndex)
    {
        for(auto widthIndex = 0; widthIndex < dieGradientImage.width(); widthIndex++)
        {
            auto index = heightIndex * dieGradientImage.width() + widthIndex;
            auto rawIndex = index*4;

            //imageRaw[rawIndex+3]; // Alpha
            imageRaw[rawIndex+2] = gradientRaw[rawIndex+2]; // Red
            imageRaw[rawIndex+1] = gradientRaw[rawIndex+1]; // Green
            imageRaw[rawIndex+0] = gradientRaw[rawIndex+0]; // Blue
        }
    }

    return dieGradientImage;
}

