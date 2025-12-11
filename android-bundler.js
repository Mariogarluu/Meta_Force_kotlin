const fs = require('fs');
const path = require('path');

// CONFIGURACIÓN
const outputFileName = 'android-context.txt';
const projectRoot = '.'; // Directorio actual

// Carpetas a ignorar (CRÍTICO en Android para no leer compilados)
const ignoredFolders = [
    '.git',
    '.gradle',
    '.idea',
    'build',
    'captures',
    'generated',
    'node_modules'
];

// Extensiones permitidas (Lógica, UI, Configuración)
const allowedExtensions = [
    '.kt',       // Kotlin
    '.java',     // Java (si tienes legacy)
    '.xml',      // Layouts, Manifest, Values
    '.gradle',   // Dependencias y configuración
    '.properties', // Gradle properties
    '.kts'       // Kotlin Script (Gradle)
];

function isIgnored(dirPath) {
    const parts = dirPath.split(path.sep);
    return parts.some(part => ignoredFolders.includes(part));
}

function scanDirectory(directory, fileList = []) {
    const files = fs.readdirSync(directory);

    files.forEach(file => {
        const filePath = path.join(directory, file);
        const stat = fs.statSync(filePath);

        if (stat.isDirectory()) {
            if (!isIgnored(filePath)) {
                scanDirectory(filePath, fileList);
            }
        } else {
            const ext = path.extname(file).toLowerCase();
            if (allowedExtensions.includes(ext)) {
                // Filtro extra: Ignorar imágenes o binarios si se colaron
                fileList.push(filePath);
            }
        }
    });

    return fileList;
}

function bundleFiles() {
    console.log('🔄 Iniciando escaneo del proyecto Android...');
    
    // Si existe el archivo previo, bórralo
    if (fs.existsSync(outputFileName)) {
        fs.unlinkSync(outputFileName);
    }

    const allFiles = scanDirectory(projectRoot);
    let outputContent = '';

    console.log(`📄 Archivos encontrados: ${allFiles.length}`);

    allFiles.forEach(file => {
        try {
            const content = fs.readFileSync(file, 'utf8');
            // Formato estándar para que yo lo entienda
            outputContent += `--- START OF FILE: ${file} ---\n`;
            outputContent += content + '\n';
            outputContent += `--- END OF FILE: ${file} ---\n\n`;
        } catch (err) {
            console.error(`❌ Error leyendo ${file}: ${err.message}`);
        }
    });

    fs.writeFileSync(outputFileName, outputContent);
    console.log(`✅ ÉXITO: Contexto generado en ${outputFileName}`);
}

bundleFiles();