#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QFileInfoList>
#include <QGraphicsPixmapItem>
#include <QGraphicsScene>
#include <QSettings>

QT_BEGIN_NAMESPACE
namespace Ui { class MainWindow; }
QT_END_NAMESPACE

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow(QWidget *parent = nullptr);
    ~MainWindow();

private slots:
    void setDieDirectory(const QString& dirString);
    void chooseDieDirectory();
    void setGradient(const QString& fileString);
    void chooseGradient();
    void combine();
    QImage applyGradient(const QImage& dieImage);

private:
    Ui::MainWindow *ui;

    QSettings mSettings;

    QFileInfoList mDieImageFiles;
    QFileInfo mGradientFile;

    QImage mDieImage;
    QImage mGradientImage;
    QImage mModifiedDieImage;

    QGraphicsPixmapItem mDieImageItem;
    QGraphicsPixmapItem mGradientImageItem;
    QGraphicsPixmapItem mModifiedDieImageItem;

    QGraphicsScene mDieScene;
    QGraphicsScene mGradientScene;
    QGraphicsScene mModifiedDieScene;
};
#endif // MAINWINDOW_H
