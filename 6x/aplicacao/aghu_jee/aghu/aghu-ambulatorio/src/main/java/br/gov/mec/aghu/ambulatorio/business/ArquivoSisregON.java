package br.gov.mec.aghu.ambulatorio.business;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacConsultasSisregDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacTipoProcedSisregDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.model.AacConsultasSisreg;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class ArquivoSisregON extends BaseBusiness {

	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	private static final Log LOG = LogFactory.getLog(ArquivoSisregON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private AacTipoProcedSisregDAO aacTipoProcedSisregDAO;
	
	@Inject
	private AacConsultasSisregDAO aacConsultasSisregDAO;
	
	private static final long serialVersionUID = 5420010611680285926L;

	private static final String NEWLINE = System.getProperty("line.separator");
	
	private enum ArquivoSisregONExceptionCode implements BusinessExceptionCode {
		ERRO_IMPORTAR_ARQUIVO_SISREG, 
		ERRO_INSERIR_CONSULTAS_SISREG, ERRO_CAMPOS_ARQUIVO_SISREG_1, ERRO_CAMPOS_ARQUIVO_SISREG_2,
		ERRO_IMPORTAR_ARQUIVO_SISREG_MOTIVO, ERRO_TIPO_PROCEDIMENTO_UNIFICADO_SISREG_NAO_ENCONTRADO,
		EXISTEM_REGISTROS_QUE_NAO_FORAM_IMPORTADOS_DO_SISREG_VERIFICAR_LOG
	}
	
	/**
	 * Realiza a importação de cada linha (consulta) do arquivo do SISREG,
	 * retornando um StringBuilder (log) contendo possiveis erros de validação.
	 */
	public void importarArquivo(String consulta, String nomeArquivo, Integer numeroLinha, StringBuilder msgErroProcedimento) throws ApplicationBusinessException{
		
		List<String> listaCamposConsulta = new ArrayList<String>();
		String aux = consulta.replace(";;", "; ;");
		aux = aux.replace(";;", "; ;");
		StringTokenizer st = new StringTokenizer(aux, ";");
		while (st.hasMoreTokens()) {
			listaCamposConsulta.add(st.nextToken());
        }
		
		if(listaCamposConsulta.size()<38){
			throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_CAMPOS_ARQUIVO_SISREG_1, nomeArquivo, numeroLinha);
		}
		if(listaCamposConsulta.size()>38){
			throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_CAMPOS_ARQUIVO_SISREG_2, nomeArquivo, numeroLinha);
		}
//		Long codInternoProced = null;
		String codUnificadoProced = null;
		try{
//			codInternoProced = Long.valueOf(listaCamposConsulta.get(1));
			codUnificadoProced = String.valueOf(listaCamposConsulta.get(2));
        } catch(NumberFormatException e){
			throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_IMPORTAR_ARQUIVO_SISREG, nomeArquivo, numeroLinha, "cod_interno_proced");
		}
		validaCodUnificadoProced(nomeArquivo, numeroLinha, msgErroProcedimento, listaCamposConsulta, codUnificadoProced);
	}
	
	private void validaCodUnificadoProced(String nomeArquivo, Integer numeroLinha, StringBuilder msgErroProcedimento,
			List<String> listaCamposConsulta, String codUnificadoProced) throws ApplicationBusinessException {
		try {
			if (codUnificadoProced.substring(0, 3).equals("030") || codUnificadoProced.substring(0, 2).equals("30")) {
				this.popularConsultaSisreg(listaCamposConsulta, nomeArquivo, numeroLinha);
				listaCamposConsulta.clear();	
			}else{
				String erroProcedimentoNaoExiste = formatarMensagemParametros("ERRO_TIPO_PROCEDIMENTO_UNIFICADO_SISREG_NAO_ENCONTRADO", 
						numeroLinha.toString(), listaCamposConsulta.get(9));
				msgErroProcedimento.append(NEWLINE);
				msgErroProcedimento.append(erroProcedimentoNaoExiste);
				msgErroProcedimento.append(": linha ");
				msgErroProcedimento.append(numeroLinha);
				listaCamposConsulta.clear();
			} 
		}catch(NumberFormatException e){
			throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_IMPORTAR_ARQUIVO_SISREG, nomeArquivo, numeroLinha, "cod_interno_proced");
		}
	}
	
	private String formatarMensagemParametros(String msgKey, Object... params) {
		String msg = getResourceBundleValue(msgKey);
		//msg = java.text.MessageFormat.format(msg, params);
		msg = MessageFormat.format(msg, params);
		return msg;
	}
	
	public void tratarErrosImportacaoConsultas(String nomeArquivo, StringBuilder msgErroProcedimento) throws ApplicationBusinessException {
		if (msgErroProcedimento.length() > 0) {
			throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.EXISTEM_REGISTROS_QUE_NAO_FORAM_IMPORTADOS_DO_SISREG_VERIFICAR_LOG, nomeArquivo, msgErroProcedimento);
		}
	}
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void popularConsultaSisreg(List<String> listaCamposConsulta, String nomeArquivo, Integer numeroLinha) throws ApplicationBusinessException{
		AacConsultasSisreg consultaSisreg = new AacConsultasSisreg();
		try{
			consultaSisreg.setSeq(Long.valueOf(listaCamposConsulta.get(0)));	
		} catch(NumberFormatException e){
			new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_IMPORTAR_ARQUIVO_SISREG, nomeArquivo, numeroLinha, "seq");
		}
		
		if(!StringUtils.isBlank(listaCamposConsulta.get(1))){
			try{
				consultaSisreg.setCodInternoProced(Long.valueOf(listaCamposConsulta.get(1)));
			}
			catch(NumberFormatException e){
				throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_IMPORTAR_ARQUIVO_SISREG, nomeArquivo, numeroLinha, "cod_interno_proced");
			}
		}
		
		if(!StringUtils.isBlank(listaCamposConsulta.get(2))){
			try{
				consultaSisreg.setCodUnificadoProced(Long.valueOf(listaCamposConsulta.get(2)));	
			} catch(NumberFormatException e){
				throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_IMPORTAR_ARQUIVO_SISREG, nomeArquivo, numeroLinha, "cod_unificado_proced");
			}
		}
		consultaSisreg.setProcedimento(listaCamposConsulta.get(3));
		if(!StringUtils.isBlank(listaCamposConsulta.get(4))){
			try{
				consultaSisreg.setCpfMedicoSolicitante(Long.valueOf(listaCamposConsulta.get(4)));	
			} catch(NumberFormatException e){
				throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_IMPORTAR_ARQUIVO_SISREG, nomeArquivo, numeroLinha, "cpf_medico_solicitante");
			}
		}
		consultaSisreg.setNomeMedicoSolicitante(listaCamposConsulta.get(5));
		if(!StringUtils.isBlank(listaCamposConsulta.get(6))){
			try{
				Integer dia = Integer.valueOf(listaCamposConsulta.get(6).substring(0, 2));
				Integer mes = Integer.valueOf(listaCamposConsulta.get(6).substring(3, 5));
				Integer ano = Integer.valueOf(listaCamposConsulta.get(6).substring(6, 10));
				Integer horaConsulta = 0;
				Integer minutoConsulta = 0;
				
				if(!StringUtils.isBlank(listaCamposConsulta.get(7))){
					horaConsulta = Integer.valueOf(listaCamposConsulta.get(7).substring(0,2));
					minutoConsulta = Integer.valueOf(listaCamposConsulta.get(7).substring(3,5));
				}
				
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DAY_OF_MONTH, dia);
				cal.set(Calendar.MONTH, mes-1);
				cal.set(Calendar.YEAR, ano);
				cal.set(Calendar.HOUR_OF_DAY, horaConsulta);
				cal.set(Calendar.MINUTE, minutoConsulta);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				consultaSisreg.setDtConsulta(cal.getTime());	
				
			} catch(Exception e){
				LOG.error(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_IMPORTAR_ARQUIVO_SISREG, nomeArquivo, numeroLinha, "dthr_consulta");
			}
		}
		
		if(!StringUtils.isBlank(listaCamposConsulta.get(8))){
			try{
				consultaSisreg.setTipoConsulta(Short.valueOf(listaCamposConsulta.get(8)));	
			} catch(NumberFormatException e){
				throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_IMPORTAR_ARQUIVO_SISREG, nomeArquivo, numeroLinha, "tipo_consulta");
			}
		}
		
		if(!StringUtils.isBlank(listaCamposConsulta.get(9))){
			try{
				consultaSisreg.setCnsPaciente(new BigInteger(listaCamposConsulta.get(9)));	
			} catch(NumberFormatException e){
				throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_IMPORTAR_ARQUIVO_SISREG, nomeArquivo, numeroLinha, "cns_paciente");
			}
		}
		consultaSisreg.setNomePaciente(listaCamposConsulta.get(10));
		if(!StringUtils.isBlank(listaCamposConsulta.get(11))){
			try{
				
				Integer dia = Integer.valueOf(listaCamposConsulta.get(11).substring(0, 2));
				Integer mes = Integer.valueOf(listaCamposConsulta.get(11).substring(3, 5));
				Integer ano = Integer.valueOf(listaCamposConsulta.get(11).substring(6, 10));
				
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DAY_OF_MONTH, dia);
				cal.set(Calendar.MONTH, mes-1);
				cal.set(Calendar.YEAR, ano);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				consultaSisreg.setDtNasc(cal.getTime());
			} catch(Exception e) {
				LOG.error(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_IMPORTAR_ARQUIVO_SISREG, nomeArquivo, numeroLinha, "dt_nasc");
			}
		}
		if(!StringUtils.isBlank(listaCamposConsulta.get(12))){
			try{
				consultaSisreg.setIdadeAnos(Short.valueOf(listaCamposConsulta.get(12)));	
			} catch(NumberFormatException e) {
				throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_IMPORTAR_ARQUIVO_SISREG, nomeArquivo, numeroLinha, "idade_anos");
			}
		}
		if(!StringUtils.isBlank(listaCamposConsulta.get(13))){
			try{
				consultaSisreg.setIdadeMeses(Short.valueOf(listaCamposConsulta.get(13)));	
			} catch(NumberFormatException e){
				throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_IMPORTAR_ARQUIVO_SISREG, nomeArquivo, numeroLinha, "idade_meses");
			}
		}
		consultaSisreg.setMaePaciente(listaCamposConsulta.get(14));
		consultaSisreg.setTipoEndereco(listaCamposConsulta.get(15));
		consultaSisreg.setLogradouro(listaCamposConsulta.get(16));
		consultaSisreg.setComplLogradouro(listaCamposConsulta.get(17));
		consultaSisreg.setNroLogradouro(listaCamposConsulta.get(18));	
		consultaSisreg.setBairro(listaCamposConsulta.get(19));
		if(!StringUtils.isBlank(listaCamposConsulta.get(20))){
			try{
				consultaSisreg.setCep(Integer.valueOf(listaCamposConsulta.get(20)));	
			} catch(NumberFormatException e){
				throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_IMPORTAR_ARQUIVO_SISREG, nomeArquivo, numeroLinha, "cep");
			}
				
		}
//		consultaSisreg.setTelContato(listaCamposConsulta.get(21));
		consultaSisreg.setNomeCidadePac(listaCamposConsulta.get(22));
		if(!StringUtils.isBlank(listaCamposConsulta.get(23))){
			try{
				consultaSisreg.setCodIbgeCidadePac(Integer.valueOf(listaCamposConsulta.get(23)));	
			} catch(NumberFormatException e){
				throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_IMPORTAR_ARQUIVO_SISREG, nomeArquivo, numeroLinha, "cod_ibge_cidade_pac");
			}
		}
		consultaSisreg.setNomeCidadeUnidSolic(listaCamposConsulta.get(24));
		if(!StringUtils.isBlank(listaCamposConsulta.get(25))){
			try{
				consultaSisreg.setCodIbgeCidadeUnidSolic(Integer.valueOf(listaCamposConsulta.get(25)));	
			} catch(NumberFormatException e) {
				throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_IMPORTAR_ARQUIVO_SISREG, nomeArquivo, numeroLinha, "cod_ibge_cidade_unid_solic");
			}
				
		}
		if(!StringUtils.isBlank(listaCamposConsulta.get(26))){
			try{
				consultaSisreg.setCnesUnidSolic(Long.valueOf(listaCamposConsulta.get(26)));	
			} catch(NumberFormatException e) {
				throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_IMPORTAR_ARQUIVO_SISREG, nomeArquivo, numeroLinha, "cnes_unid_solic");
			}
		}
		consultaSisreg.setNomeUnidSolic(listaCamposConsulta.get(27));
		consultaSisreg.setSexo(listaCamposConsulta.get(28));
		if(!StringUtils.isBlank(listaCamposConsulta.get(29))){
			try{
			Integer dia = Integer.valueOf(listaCamposConsulta.get(29).substring(0, 2));
			Integer mes = Integer.valueOf(listaCamposConsulta.get(29).substring(3, 5));
			Integer ano = Integer.valueOf(listaCamposConsulta.get(29).substring(6, 10));
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, dia);
			cal.set(Calendar.MONTH, mes-1);
			cal.set(Calendar.YEAR, ano);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			consultaSisreg.setDataSolicitante(cal.getTime());	
			} catch(Exception e){
				LOG.error(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_IMPORTAR_ARQUIVO_SISREG, nomeArquivo, numeroLinha, "data_solicitante");
			}
		}
		consultaSisreg.setOperadorSolicitante(listaCamposConsulta.get(30));
		if(!StringUtils.isBlank(listaCamposConsulta.get(31))){
			try{
				Integer dia = Integer.valueOf(listaCamposConsulta.get(31).substring(0, 2));
				Integer mes = Integer.valueOf(listaCamposConsulta.get(31).substring(3, 5));
				Integer ano = Integer.valueOf(listaCamposConsulta.get(31).substring(6, 10));
				
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DAY_OF_MONTH, dia);
				cal.set(Calendar.MONTH, mes-1);
				cal.set(Calendar.YEAR, ano);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				consultaSisreg.setDataAutorizacao(cal.getTime());
			} catch(Exception e){
				LOG.error(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_IMPORTAR_ARQUIVO_SISREG, nomeArquivo, numeroLinha, "data_autorizacao");
			}
		}
		consultaSisreg.setOperadorAutorizador(listaCamposConsulta.get(32));
		if(!StringUtils.isBlank(listaCamposConsulta.get(33))){
			try{
				consultaSisreg.setValorProcedimento(new BigDecimal(listaCamposConsulta.get(33)));	
			} catch(NumberFormatException e){
				throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_IMPORTAR_ARQUIVO_SISREG, nomeArquivo, numeroLinha, "valor_procedimento");
			}
			
		}
		consultaSisreg.setSituacao(listaCamposConsulta.get(34));
		consultaSisreg.setCid(listaCamposConsulta.get(35));
		if(!StringUtils.isBlank(listaCamposConsulta.get(36))){
			try{
				consultaSisreg.setCpfSolicitante(Long.valueOf(listaCamposConsulta.get(36)));	
			} catch(NumberFormatException e){
				throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_IMPORTAR_ARQUIVO_SISREG, nomeArquivo, numeroLinha, "cpf_solicitante");
			}
		}
		consultaSisreg.setNomeSolicitante(listaCamposConsulta.get(37));
		consultaSisreg.setMarcado(false);
		consultaSisreg.setLinhaArquivo(numeroLinha);
		try{
			this.inserirConsultaSisreg(consultaSisreg);	
		} catch(Exception e){
			LOG.error(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(ArquivoSisregONExceptionCode.ERRO_INSERIR_CONSULTAS_SISREG);
		}
	}
	
	public void inserirConsultaSisreg(AacConsultasSisreg consultaSisreg){
		this.getAacConsultasSisregDAO().persistir(consultaSisreg);
		this.getAacConsultasSisregDAO().flush();
	}
	
	protected AacTipoProcedSisregDAO getAacTipoProcedSisregDAO() {
		return aacTipoProcedSisregDAO;
	}
	
	protected AacConsultasSisregDAO getAacConsultasSisregDAO() {
		return aacConsultasSisregDAO;
	}
	
	public void limparConsultasSisreg(){
		List<AacConsultasSisreg> listaConsultasSisreg = aacConsultasSisregDAO.pesquisarConsultasSisreg();
		for(AacConsultasSisreg consultasSisreg: listaConsultasSisreg){
			this.getAacConsultasSisregDAO().remover(consultasSisreg);
			this.getAacConsultasSisregDAO().flush();
		}
	}
	
	
}
