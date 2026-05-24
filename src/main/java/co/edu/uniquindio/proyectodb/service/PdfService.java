package co.edu.uniquindio.proyectodb.service;

import co.edu.uniquindio.proyectodb.model.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Generación de reportes en PDF.
 *
 * REPORTES DISPONIBLES:
 * 1. generarReporteJugadores → lista completa o filtrada de jugadores
 * 2. generarReporteEquipos → lista de equipos con valor total
 * 3. generarReporteDirectoresTecnicos → lista de DTs
 * 4. generarReportePartidos → calendario de partidos con resultado
 * 5. generarReporteBitacora → historial de sesiones
 *
 */
public class PdfService {

    // -------------------------------------------------------------------------
    // ESTILOS COMPARTIDOS
    // -------------------------------------------------------------------------

    private static final BaseColor COLOR_ENCABEZADO = new BaseColor(0, 51, 102);
    private static final BaseColor COLOR_FILA_PAR = new BaseColor(220, 230, 242);
    private static final BaseColor COLOR_FILA_IMPAR = BaseColor.WHITE;
    private static final BaseColor COLOR_TITULO_TABLA = new BaseColor(0, 102, 153);

    private static final Font FUENTE_TITULO = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.WHITE);
    private static final Font FUENTE_SUBTIT = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, COLOR_ENCABEZADO);
    private static final Font FUENTE_ENCAB = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE);
    private static final Font FUENTE_CELDA = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.DARK_GRAY);
    private static final Font FUENTE_FOOTER = new Font(Font.FontFamily.HELVETICA, 7, Font.ITALIC, BaseColor.GRAY);

    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // =========================================================================
    // 1. REPORTE DE JUGADORES
    // =========================================================================

    /**
     * Genera un PDF con la lista de jugadores proporcionada.
     * El título del reporte se puede personalizar para indicar el filtro aplicado
     *
     * @param jugadores  lista de jugadores a incluir
     * @param titulo     título descriptivo que aparecerá en el encabezado
     * @param rutaSalida ruta completa del archivo PDF a generar
     */
    public void generarReporteJugadores(List<Jugador> jugadores, String titulo, String rutaSalida) {
        Document doc = new Document(PageSize.A4.rotate(), 30, 30, 60, 40);
        try {
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(rutaSalida));
            writer.setPageEvent(new PieDePageina("Reporte de Jugadores"));
            doc.open();

            agregarEncabezado(doc, titulo, jugadores.size() + " registro(s)");

            // Columnas: Nombre | Posición | Fecha Nac. | Peso | Estatura | Valor Mercado |
            // Equipo(ID)
            float[] anchos = { 3f, 1.8f, 1.5f, 1f, 1f, 2f, 1.2f };
            PdfPTable tabla = crearTabla(anchos);

            agregarEncabezadoTabla(tabla,
                    "Nombre", "Posición", "Fecha Nac.", "Peso (kg)",
                    "Estatura (m)", "Valor Mercado", "ID Equipo");

            for (int i = 0; i < jugadores.size(); i++) {
                Jugador j = jugadores.get(i);
                BaseColor fondo = (i % 2 == 0) ? COLOR_FILA_PAR : COLOR_FILA_IMPAR;

                agregarFila(tabla, fondo,
                        j.getNombre(),
                        j.getPosicion(),
                        j.getFechaNacimiento() != null ? j.getFechaNacimiento().format(FMT_FECHA) : "-",
                        j.getPeso() != null ? j.getPeso().toPlainString() : "-",
                        j.getEstatura() != null ? j.getEstatura().toPlainString() : "-",
                        j.getValorMercado() != null ? "$" + formatearNumero(j.getValorMercado().longValue()) : "-",
                        String.valueOf(j.getIdEquipo()));
            }

            doc.add(tabla);
            doc.close();
            System.out.println("[PdfService] Reporte de jugadores generado: " + rutaSalida);

        } catch (DocumentException | IOException e) {
            System.err.println("[PdfService] Error al generar reporte de jugadores: " + e.getMessage());
        }
    }

    // =========================================================================
    // 2. REPORTE DE EQUIPOS
    // =========================================================================

    /**
     * Genera un PDF con la lista de equipos.
     *
     * @param equipos    lista de equipos
     * @param titulo     título descriptivo
     * @param rutaSalida ruta del PDF de salida
     */
    public void generarReporteEquipos(List<Equipo> equipos, String titulo, String rutaSalida) {
        Document doc = new Document(PageSize.A4, 30, 30, 60, 40);
        try {
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(rutaSalida));
            writer.setPageEvent(new PieDePageina("Reporte de Equipos"));
            doc.open();

            agregarEncabezado(doc, titulo, equipos.size() + " equipo(s)");

            float[] anchos = { 3f, 2f, 2.5f, 1.5f };
            PdfPTable tabla = crearTabla(anchos);

            agregarEncabezadoTabla(tabla, "Nombre", "País", "Valor Total Equipo", "ID Confederación");

            for (int i = 0; i < equipos.size(); i++) {
                Equipo e = equipos.get(i);
                BaseColor fondo = (i % 2 == 0) ? COLOR_FILA_PAR : COLOR_FILA_IMPAR;
                agregarFila(tabla, fondo,
                        e.getNombre(),
                        e.getPais(),
                        e.getValorTotalEquipo() != null
                                ? "$" + formatearNumero(e.getValorTotalEquipo().longValue())
                                : "-",
                        String.valueOf(e.getIdConfederacion()));
            }

            doc.add(tabla);
            doc.close();
            System.out.println("[PdfService] Reporte de equipos generado: " + rutaSalida);

        } catch (DocumentException | IOException e) {
            System.err.println("[PdfService] Error al generar reporte de equipos: " + e.getMessage());
        }
    }

    // =========================================================================
    // 3. REPORTE DE DIRECTORES TÉCNICOS
    // =========================================================================

    /**
     * Genera un PDF con la lista de Directores Técnicos.
     *
     * @param directores lista de directores técnicos
     * @param titulo     título descriptivo
     * @param rutaSalida ruta del PDF de salida
     */
    public void generarReporteDirectoresTecnicos(List<DirectorTecnico> directores,
            String titulo, String rutaSalida) {
        Document doc = new Document(PageSize.A4, 30, 30, 60, 40);
        try {
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(rutaSalida));
            writer.setPageEvent(new PieDePageina("Reporte de Directores Técnicos"));
            doc.open();

            agregarEncabezado(doc, titulo, directores.size() + " director(es)");

            float[] anchos = { 3f, 2f, 1.8f, 1.5f };
            PdfPTable tabla = crearTabla(anchos);

            agregarEncabezadoTabla(tabla, "Nombre", "Nacionalidad", "Fecha Nac.", "ID Equipo");

            for (int i = 0; i < directores.size(); i++) {
                DirectorTecnico dt = directores.get(i);
                BaseColor fondo = (i % 2 == 0) ? COLOR_FILA_PAR : COLOR_FILA_IMPAR;
                agregarFila(tabla, fondo,
                        dt.getNombre(),
                        dt.getNacionalidad(),
                        dt.getFechaNacimiento() != null ? dt.getFechaNacimiento().format(FMT_FECHA) : "-",
                        String.valueOf(dt.getIdEquipo()));
            }

            doc.add(tabla);
            doc.close();
            System.out.println("[PdfService] Reporte de directores técnicos generado: " + rutaSalida);

        } catch (DocumentException | IOException e) {
            System.err.println("[PdfService] Error al generar reporte de DTs: " + e.getMessage());
        }
    }

    // =========================================================================
    // 4. REPORTE DE PARTIDOS
    // =========================================================================

    /**
     * Genera un PDF con el calendario/resultados de partidos.
     *
     * @param partidos   lista de partidos
     * @param titulo     título descriptivo
     * @param rutaSalida ruta del PDF de salida
     */
    public void generarReportePartidos(List<Partido> partidos, String titulo, String rutaSalida) {
        Document doc = new Document(PageSize.A4.rotate(), 30, 30, 60, 40);
        try {
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(rutaSalida));
            writer.setPageEvent(new PieDePageina("Reporte de Partidos"));
            doc.open();

            agregarEncabezado(doc, titulo, partidos.size() + " partido(s)");

            // Columnas: Fecha/Hora | Local(ID) | Resultado | Visitante(ID) | Estadio(ID) |
            // Grupo(ID)
            float[] anchos = { 2f, 1.5f, 1.5f, 1.5f, 1.5f, 1f };
            PdfPTable tabla = crearTabla(anchos);

            agregarEncabezadoTabla(tabla,
                    "Fecha y Hora", "ID Eq. Local", "Resultado", "ID Eq. Visit.", "ID Estadio", "ID Grupo");

            for (int i = 0; i < partidos.size(); i++) {
                Partido p = partidos.get(i);
                BaseColor fondo = (i % 2 == 0) ? COLOR_FILA_PAR : COLOR_FILA_IMPAR;

                String resultado = p.getGolesLocal() + " - " + p.getGolesVisitante();

                agregarFila(tabla, fondo,
                        p.getFechaHora() != null ? p.getFechaHora().format(FMT_FECHA_HORA) : "-",
                        String.valueOf(p.getIdEquipoLocal()),
                        resultado,
                        String.valueOf(p.getIdEquipoVisitante()),
                        String.valueOf(p.getIdEstadio()),
                        String.valueOf(p.getIdGrupo()));
            }

            doc.add(tabla);
            doc.close();
            System.out.println("[PdfService] Reporte de partidos generado: " + rutaSalida);

        } catch (DocumentException | IOException e) {
            System.err.println("[PdfService] Error al generar reporte de partidos: " + e.getMessage());
        }
    }

    // =========================================================================
    // 5. REPORTE DE BITÁCORA
    // =========================================================================

    /**
     * Genera un PDF con el historial de sesiones de la bitácora.
     * Solo debe invocarse cuando el usuario activo es Administrador.
     *
     * @param registros  lista de registros de bitácora
     * @param titulo     título descriptivo
     * @param rutaSalida ruta del PDF de salida
     */
    public void generarReporteBitacora(List<Bitacora> registros, String titulo, String rutaSalida) {
        Document doc = new Document(PageSize.A4, 30, 30, 60, 40);
        try {
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(rutaSalida));
            writer.setPageEvent(new PieDePageina("Reporte de Bitácora"));
            doc.open();

            agregarEncabezado(doc, titulo, registros.size() + " registro(s)");

            float[] anchos = { 1f, 2.5f, 2.5f, 1.5f };
            PdfPTable tabla = crearTabla(anchos);

            agregarEncabezadoTabla(tabla, "ID Usuario", "Fecha/Hora Ingreso", "Fecha/Hora Salida", "Estado");

            for (int i = 0; i < registros.size(); i++) {
                Bitacora b = registros.get(i);
                BaseColor fondo = (i % 2 == 0) ? COLOR_FILA_PAR : COLOR_FILA_IMPAR;

                String salida = b.getFechaHoraSalida() != null
                        ? b.getFechaHoraSalida().format(FMT_FECHA_HORA)
                        : "Activa";
                String estado = b.isSesionActiva() ? "ACTIVA" : "CERRADA";

                agregarFila(tabla, fondo,
                        String.valueOf(b.getIdUsuario()),
                        b.getFechaHoraIngreso() != null ? b.getFechaHoraIngreso().format(FMT_FECHA_HORA) : "-",
                        salida,
                        estado);
            }

            doc.add(tabla);
            doc.close();
            System.out.println("[PdfService] Reporte de bitácora generado: " + rutaSalida);

        } catch (DocumentException | IOException e) {
            System.err.println("[PdfService] Error al generar reporte de bitácora: " + e.getMessage());
        }
    }

    // =========================================================================
    // HELPERS PRIVADOS — construcción del documento
    // =========================================================================

    /**
     * Agrega el bloque de encabezado visual al documento:
     * rectángulo de color + título + subtítulo con conteo.
     */
    private void agregarEncabezado(Document doc, String titulo, String subtitulo)
            throws DocumentException {

        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        header.setSpacingAfter(12);

        PdfPCell celdaTitulo = new PdfPCell(new Phrase("  Mundial 2026  —  " + titulo, FUENTE_TITULO));
        celdaTitulo.setBackgroundColor(COLOR_ENCABEZADO);
        celdaTitulo.setBorder(Rectangle.NO_BORDER);
        celdaTitulo.setPadding(12);
        celdaTitulo.setHorizontalAlignment(Element.ALIGN_LEFT);
        header.addCell(celdaTitulo);
        doc.add(header);

        String fechaGen = "Generado: " + java.time.LocalDateTime.now().format(FMT_FECHA_HORA);
        Paragraph sub = new Paragraph(subtitulo + "     |     " + fechaGen, FUENTE_SUBTIT);
        sub.setSpacingAfter(8);
        doc.add(sub);
    }

    /**
     * Crea una tabla con los anchos relativos de columna indicados,
     * ocupando el 100% del ancho de la página.
     */
    private PdfPTable crearTabla(float[] anchos) throws DocumentException {
        PdfPTable tabla = new PdfPTable(anchos.length);
        tabla.setWidthPercentage(100);
        tabla.setWidths(anchos);
        tabla.setSpacingBefore(4);
        tabla.setHeaderRows(1);
        return tabla;
    }

    /**
     * Agrega la fila de encabezados de la tabla con fondo de color.
     *
     * @param tabla  tabla destino
     * @param textos textos de cada columna en orden
     */
    private void agregarEncabezadoTabla(PdfPTable tabla, String... textos) {
        for (String texto : textos) {
            PdfPCell celda = new PdfPCell(new Phrase(texto, FUENTE_ENCAB));
            celda.setBackgroundColor(COLOR_TITULO_TABLA);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setPadding(6);
            celda.setBorderColor(BaseColor.WHITE);
            tabla.addCell(celda);
        }
    }

    /**
     * Agrega una fila de datos a la tabla con el color de fondo indicado.
     *
     * @param tabla  tabla destino
     * @param fondo  color de fondo de la fila
     * @param textos valores de cada celda en orden
     */
    private void agregarFila(PdfPTable tabla, BaseColor fondo, String... textos) {
        for (String texto : textos) {
            PdfPCell celda = new PdfPCell(new Phrase(texto != null ? texto : "-", FUENTE_CELDA));
            celda.setBackgroundColor(fondo);
            celda.setPadding(5);
            celda.setBorderColor(new BaseColor(200, 210, 220));
            tabla.addCell(celda);
        }
    }

    /**
     * Formatea un número largo con separadores de miles.
     * Ejemplo: 150000000 → "150.000.000"
     */
    private String formatearNumero(long numero) {
        return String.format("%,d", numero).replace(",", ".");
    }

    // =========================================================================
    // CLASE INTERNA — Pie de página con número de página
    // =========================================================================

    /**
     * PdfPageEventHelper que escribe el pie de página en cada página.
     *
     * onEndPage() se llama automáticamente por iTextPDF al cerrar cada página.
     * Escribe el nombre del reporte a la izquierda y "Página X de Y" a la derecha.
     *
     */
    private static class PieDePageina extends PdfPageEventHelper {

        private final String nombreReporte;
        private PdfTemplate totalPaginasTemplate;
        private BaseFont fuenteBase;

        PieDePageina(String nombreReporte) {
            this.nombreReporte = nombreReporte;
        }

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            try {
                fuenteBase = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
                totalPaginasTemplate = writer.getDirectContent().createTemplate(30, 12);
            } catch (DocumentException | IOException e) {
                System.err.println("[PdfService] Error al crear fuente de pie: " + e.getMessage());
            }
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            float y = document.bottom() - 10;

            cb.beginText();
            cb.setFontAndSize(fuenteBase, 7);
            cb.setColorFill(BaseColor.GRAY);
            cb.setTextMatrix(document.left(), y);
            cb.showText(nombreReporte + "  —  Sistema Mundial 2026");
            cb.endText();

            String textoPag = "Página " + writer.getPageNumber() + " de ";
            float anchoPag = fuenteBase.getWidthPoint(textoPag, 7);
            cb.beginText();
            cb.setFontAndSize(fuenteBase, 7);
            cb.setColorFill(BaseColor.GRAY);
            cb.setTextMatrix(document.right() - anchoPag - 30, y);
            cb.showText(textoPag);
            cb.endText();

            cb.addTemplate(totalPaginasTemplate, document.right() - 30, y);
        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            totalPaginasTemplate.beginText();
            totalPaginasTemplate.setFontAndSize(fuenteBase, 7);
            totalPaginasTemplate.setColorFill(BaseColor.GRAY);
            totalPaginasTemplate.setTextMatrix(0, 0);
            totalPaginasTemplate.showText(String.valueOf(writer.getPageNumber() - 1));
            totalPaginasTemplate.endText();
        }
    }
}