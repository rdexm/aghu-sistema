package br.gov.mec.aghu.transplante.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.dominio.DominioTipoTransplanteCombo;
import br.gov.mec.aghu.transplante.vo.FiltroTempoPermanenciaListVO;
import br.gov.mec.aghu.transplante.vo.FiltroTempoSobrevidaTransplanteVO;
import br.gov.mec.aghu.transplante.vo.RelatorioExtratoTransplantesPacienteVO;
import br.gov.mec.aghu.transplante.vo.RelatorioPermanenciaPacienteListaTransplanteVO;
import br.gov.mec.aghu.transplante.vo.RelatorioSobrevidaPacienteTransplanteVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatoriosTransplanteON extends BaseBusiness {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7832976271916159133L;
		private static final Log LOG = LogFactory.getLog(RelatoriosTransplanteON.class);

		private static final String ENCODE = "ISO-8859-1";
		private static final String SEPARADOR=";";
		private static final String QUEBRA_LINHA = "\n";
		private static final String BARRA = "  |  ";
		private static final String PRONTUARIO = "Prontuário";
		private static final String NOME = "Nome";
		private static final String DT_INGRESSO = "Dt. Ingresso";
		private static final String PERMANENCIA = "Permanência";
		private static final String TIPO = "Tipo";
		private static final String PATTER_DATA = "dd/MM/yyyy";
		private static final Integer P_AGHU_DIAS_SOBREVIDA_APOS_TRANSP = 365;
		
		
		public void validarRegrasTelaRelatorioFilaTransplante(FiltroTempoPermanenciaListVO filtro) throws BaseListException {
			BaseListException lista = new BaseListException();
			
			if(filtro.getDataInicio() != null && filtro.getDataFim() == null){
				filtro.setDataFim(new Date());
			}
			
			if(filtro.getDataFim() != null && filtro.getDataInicio() == null){
				filtro.setDataInicio(new Date());
			}
			
			if(DateUtil.validaDataMaior(filtro.getDataInicio(), filtro.getDataFim())){
				lista.add(new ApplicationBusinessException(MtxFilaTransplanteONExceptionCode.DATA_INVALIDA));
			}
			
			if(DateUtil.obterQtdDiasEntreDuasDatas(filtro.getDataInicio(),filtro.getDataFim()) > P_AGHU_DIAS_SOBREVIDA_APOS_TRANSP) {
				lista.add(new ApplicationBusinessException(MtxFilaTransplanteONExceptionCode.DATA_EXCEDIDA));
			}
			
			if (filtro.getTipoTransplante() == null) {
				lista.add(new ApplicationBusinessException(MtxFilaTransplanteONExceptionCode.REL_TRANSPLANTE_TIPO_OBRIGATORIO));
			} 
			if(lista.hasException()){
				throw lista;
			}
		}
		
		
		public boolean verificaTipoTransplanteRelatorioFila(FiltroTempoPermanenciaListVO filtro){
			if(filtro.getTipoTransplante() != null) {
				if(filtro.getTipoTransplante().equals(DominioTipoTransplanteCombo.O)){
					return true;
				} else {
					return false;
				}
			}
			return false;
		}
		
		public boolean verificaTipoTMOFilaTransplante(FiltroTempoPermanenciaListVO filtro){
			if(filtro.getTipoTMO() != null) {
				if(filtro.getTipoTMO().equals(DominioSituacaoTmo.G)){
					return false;
				}
			}
			return true;
		}
		
		
		/**
		 * CVS
		 */
		public String gerarRelatorioTempoPermanenciaCSV(List<RelatorioPermanenciaPacienteListaTransplanteVO> colecao) throws BaseException, IOException{
			final File file = File.createTempFile(DominioNomeRelatorio.RELATORIO_CVS_TRANSPLANTES_TEMPO_FILA.getDescricao(), ".cvs");
			final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
			
			out.write("Prontuário"+SEPARADOR+"Nome"+SEPARADOR+"Tipo"+SEPARADOR+"Permanência"+SEPARADOR+
			"Escore"+SEPARADOR+"Fases"+QUEBRA_LINHA);
			
			for(RelatorioPermanenciaPacienteListaTransplanteVO current : colecao) {
				verificarValoresNulosListaPermanencia(current);
				StringBuilder fasesString = new StringBuilder();
				if(current.getFases()!= null){
					for (int i = 0; i < current.getFases().size(); i++) {
						if(current.getFases().size() > 1){
							if(i < (current.getFases().size() -1)){
								fasesString.append(current.getFases().get(i).getTextoConcatenado()+BARRA);
							}else{
								fasesString.append(current.getFases().get(i).getTextoConcatenado());
							}
						}else{
							fasesString.append(current.getFases().get(i).getTextoConcatenado());
						}
					}
				}
				out.write(current.getProntuario()+SEPARADOR+current.getNome()+SEPARADOR+
						current.getTipo()+SEPARADOR+current.getPermanencia()+SEPARADOR+current.getEscore()+SEPARADOR+fasesString+QUEBRA_LINHA);
			}
			out.flush();
			out.close();
			return file.getAbsolutePath();
		}


		private void verificarValoresNulosListaPermanencia(
				RelatorioPermanenciaPacienteListaTransplanteVO current) {
			if(current.getPermanencia() == null){
				current.setPermanencia("");
			}
			if(current.getProntuario() == null){
				current.setProntuario("");
			}
			if(current.getEscore() == null){
				current.setEscore("");
			}
		}
		/**CV $41792
		 */
		public String gerarRelatorioSobrevidaCSV(List<RelatorioSobrevidaPacienteTransplanteVO> colecao) throws BaseException, IOException{
			final File file = File.createTempFile(DominioNomeRelatorio.RELATORIO_CVS_TRANSPLANTES_SOBREVIDA.getDescricao(), ".cvs");
			final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
			out.write("Prontuário"+SEPARADOR+"Nome"+SEPARADOR+TIPO+SEPARADOR+"Data do Transplante"+SEPARADOR+
			"Data do Último Atendimento"+SEPARADOR+"Sobrevida"+QUEBRA_LINHA);
			for(RelatorioSobrevidaPacienteTransplanteVO current : colecao) {
				if(current.getDataUltimoAtendimento() == null){
					current.setDataUltimoAtendimento("");
				}
				if(current.getSobrevida() == null){
					current.setSobrevida("");
				}
				out.write(current.getProntuario()+SEPARADOR+current.getNome()+SEPARADOR+
						current.getTipo()+SEPARADOR+current.getDataTransplante()+SEPARADOR+current.getDataUltimoAtendimento()+SEPARADOR+current.getSobrevida()+QUEBRA_LINHA);
			}
			out.flush();
			out.close();
			return file.getAbsolutePath();
		}


		@Override
		protected Log getLogger() {
			return LOG;
		}
		
		/**
		 * #41792
		 * @throws BaseListException 
		 */
		public void validarRegrasTelaRelatorioSobrevidaTransplante(FiltroTempoSobrevidaTransplanteVO filtro) throws BaseListException{
			if(filtro.getDataInicio() != null && filtro.getDataFim() == null){
				filtro.setDataFim(new Date());
			}
			BaseListException lista = new BaseListException();
			if(filtro.getDataFim() != null && filtro.getDataInicio() == null){
				filtro.setDataInicio(new Date());
			}
			
			if(DateUtil.validaDataMaior(filtro.getDataInicio(), filtro.getDataFim())){
				lista.add(new ApplicationBusinessException(MtxFilaTransplanteONExceptionCode.DATA_INVALIDA));
			}
			
			if(DateUtil.obterQtdDiasEntreDuasDatas(filtro.getDataInicio(),filtro.getDataFim()) > P_AGHU_DIAS_SOBREVIDA_APOS_TRANSP) {
				lista.add(new ApplicationBusinessException(MtxFilaTransplanteONExceptionCode.DATA_EXCEDIDA));
			}
							
			if (filtro.getTipoTransplante() == null) {
				lista.add(new ApplicationBusinessException(MtxFilaTransplanteONExceptionCode.REL_TRANSPLANTE_TIPO_OBRIGATORIO));
				
			}
			if(lista.hasException()){
				throw lista;
			}
		}
		
		private enum MtxFilaTransplanteONExceptionCode implements BusinessExceptionCode{
			DATA_INVALIDA,
			REL_TRANSPLANTE_TIPO_OBRIGATORIO,
			DATA_EXCEDIDA;
		}
		
		public String gerarRelatorioExtratoTransplantePacienteCSV(List<RelatorioExtratoTransplantesPacienteVO> colecao) throws BaseException, IOException{
			Date date = new Date();
			final File file = File.createTempFile(DominioNomeRelatorio.MTXR_EXTRATO_TRANSPLANTES.toString()+DateUtil.dataToString(date, "yyyyMMddHHmm"), ".csv");
			final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
			out.write(PRONTUARIO+SEPARADOR+NOME+SEPARADOR+"Sexo"+SEPARADOR+"Idade"+SEPARADOR+
					DT_INGRESSO+SEPARADOR+TIPO+SEPARADOR+"Data"+SEPARADOR+"Situação"+SEPARADOR+
					"Motivo"+SEPARADOR+"Responsável"+SEPARADOR+QUEBRA_LINHA);
			
			for(RelatorioExtratoTransplantesPacienteVO current : colecao) {
				
				out.write((current.getProntuarioFormatado() != null?current.getProntuarioFormatado():"")+SEPARADOR+current.getNome()+SEPARADOR+
						current.getSexoFormatado()+SEPARADOR+current.getIdadeFormatada()+SEPARADOR+
						DateUtil.dataToString(current.getDataIngresso(), PATTER_DATA)+SEPARADOR+(current.getColunaTipo()!=null?current.getColunaTipo():"")+SEPARADOR+
						DateUtil.dataToString(current.getDataOcorrencia(), PATTER_DATA)+SEPARADOR+current.getSituacaoFormatado()+SEPARADOR+
						(current.getDescricaoMotivo()!=null?current.getDescricaoMotivo():"")+SEPARADOR+(current.getNomeResponsavel() != null?current.getNomeResponsavel():"")
						+SEPARADOR+QUEBRA_LINHA);
			}
			out.flush();
			out.close();
			return file.getAbsolutePath();
		}
		
		
		public String gerarRelatorioPacientesComObitoListaEsperaTranplenteCSV(List<RelatorioExtratoTransplantesPacienteVO> colecao) throws BaseException, IOException{
			Date date = new Date();
			final File file = File.createTempFile(DominioNomeRelatorio.RELATORIO_CVS_TRANSPLANTES_TEMPO_FILA.toString()+DateUtil.dataToString(date, "yyyyMMddHHmm"), ".csv");
			final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
			out.write(PRONTUARIO+SEPARADOR+NOME+SEPARADOR+DT_INGRESSO+SEPARADOR+"Dt. Óbito"+SEPARADOR+TIPO+SEPARADOR+
					PERMANENCIA+SEPARADOR+QUEBRA_LINHA);
			
			for(RelatorioExtratoTransplantesPacienteVO current : colecao) {
				
				out.write((current.getProntuarioFormatado() != null?current.getProntuarioFormatado():"")+SEPARADOR+current.getNome()+SEPARADOR+
						DateUtil.dataToString(current.getDataIngresso(), PATTER_DATA)+SEPARADOR+
						DateUtil.dataToString(current.getDataObito(), PATTER_DATA)+SEPARADOR+current.getColunaTipo()+SEPARADOR+
						current.getPermanencia()+(current.getPermanencia()>1?" dias":" dia")+SEPARADOR+QUEBRA_LINHA);
			}
			out.flush();
			out.close();
			return file.getAbsolutePath();
		}
		
		
	public List<RelatorioExtratoTransplantesPacienteVO> formatarExtratoPacienteTransplante(List<RelatorioExtratoTransplantesPacienteVO> listaRelatorio){
			
			for(RelatorioExtratoTransplantesPacienteVO item : listaRelatorio){
				if(item.getDtNascimento() != null){
					int i = DateUtil.obterQtdAnosEntreDuasDatas(item.getDtNascimento(), new Date());
					item.setIdadeFormatada(i+" anos");
				}
				item.setSexoFormatado(item.getSexo() !=null?item.getSexo().getDescricao():"");
				item = executaRegraColunaTipo(item);
				if(item.getSituacao()!=null){
					item.setSituacaoFormatado(item.getSituacao().retornarDescricaoCompleta());
				}
				if(item.getProntuario() != null){
					item.setProntuarioFormatado(CoreUtil.formataProntuario(item.getProntuario()));
				}
				if(item.getDataObito()!=null && item.getDataIngresso() != null){
					item.setPermanencia(DateUtil.obterQtdDiasEntreDuasDatas(item.getDataIngresso(), item.getDataObito()));
				}
			}
			
			return listaRelatorio;
		}


		private RelatorioExtratoTransplantesPacienteVO executaRegraColunaTipo(
				RelatorioExtratoTransplantesPacienteVO item) {
			if(item.getTipoOrgao() != null ){
				item.setColunaTipo(item.getTipoOrgao().getDescricao());
			} else if(item.getTipoTmo() != null){
				if(item.getTipoTmo().equals(DominioSituacaoTmo.U)){
					item.setColunaTipo(item.getTipoTmo().getDescricao());
				}
				else{
					item.setColunaTipo(item.getTipoTmo().getDescricao()+" - "+item.getTipoAlogenico().getDescricao());
				}
			}
			return item;
		
		}
		
		/**
		 *  Remover repetidos #49536
		 * @param listaRelatorio
		 * @return
		 */
		public List<RelatorioExtratoTransplantesPacienteVO> removerRepetidosLista(List<RelatorioExtratoTransplantesPacienteVO> listaRelatorio){
			List<RelatorioExtratoTransplantesPacienteVO> listaLimpa = new ArrayList<RelatorioExtratoTransplantesPacienteVO>();
			for(int i=0;i<listaRelatorio.size();i++){
					if(listaLimpa.isEmpty()){
						listaLimpa.add(listaRelatorio.get(0));
					}
					int repeticoes=0;
					for(RelatorioExtratoTransplantesPacienteVO item:listaLimpa){
						
						if(item.getPacCodigo().equals(listaRelatorio.get(i).getPacCodigo())){
							repeticoes++;
						}
					}
					if(repeticoes==0){
						listaLimpa.add(listaRelatorio.get(i));
					}
									
			}
			
			return  listaLimpa;
		}
		
		public String validarDatas(FiltroTempoPermanenciaListVO filtro,int numeroMaximo){
			if(DateUtil.obterQtdDiasEntreDuasDatas(filtro.getDataInicio(), filtro.getDataFim())>numeroMaximo){
				return "O Período de Óbito não pode exceder "+ numeroMaximo+" dias.";
				
			}
			
			if(DateUtil.validaDataMaior(filtro.getDataInicio(), filtro.getDataFim())){
				return "A Data Inicial não pode ser superior à Data Final.";
			}
			
			return "";
			
		}

}
