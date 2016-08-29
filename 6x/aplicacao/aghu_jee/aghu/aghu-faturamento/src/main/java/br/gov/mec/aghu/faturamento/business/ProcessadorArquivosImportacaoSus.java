package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessadorArquivosImportacaoSus extends BaseBMTBusiness {
	
	private static final long serialVersionUID = 7439743103164192223L;

	private static final Log LOG = LogFactory.getLog(ProcessadorArquivosImportacaoSus.class);
	
	@EJB
	private ProcessadorArquivosImportacaoSusCBOProcON processadorArquivosImportacaoSusCBOProcON;
	
	@EJB
	private ProcessadorArquivosImportacaoSusCIDProcON processadorArquivosImportacaoSusCIDProcON;
	
	@EJB
	private ProcessadorArquivosImportacaoSusProcHospON processadorArquivosImportacaoSusProcHospON;
	
	@EJB
	private ProcessadorArquivosImportacaoSusGruposON processadorArquivosImportacaoSusGruposON;
	
	@EJB
	private ProcessadorArquivosImportacaoSusSubGruposON processadorArquivosImportacaoSusSubGruposON;
	
	@EJB
	private ProcessadorArquivosImportacaoSusModalidadeON processadorArquivosImportacaoSusModalidadeON;
	
	@EJB
	private ProcessadorArquivosImportacaoSusFormasOrganizacaoON processadorArquivosImportacaoSusFormasOrganizacaoON;
	
	@EJB
	private ProcessadorArquivosImportacaoSusCompatibilidadeON processadorArquivosImportacaoSusCompatibilidadeON;
	
	@EJB
	private ProcessadorArquivosImportacaoSusProcedServicoON processadorArquivosImportacaoSusProcedServicoON;
	
	@EJB
	private ProcessadorArquivosImportacaoSusFinanciamentoON processadorArquivosImportacaoSusFinanciamentoON;
	
	@EJB
	private ProcessadorArquivosImportacaoSusProcedRegistroON processadorArquivosImportacaoSusProcedRegistroON;
	
	protected enum ImportarArquivoSusONExceptionCode implements BusinessExceptionCode {
		ERRO_LIMPAR_PROCEDIMENTOS_CBO, MSG_ITENS_PROCEDIMENTO_NAO_ENCONTRADO, MSG_INICIO_PROCESSAMENTO, MSG_FIM_PROCESSAMENTO, 
		MSG_EXCLUSAO_PROCEDIMENTOS, MSG_ERRO_LEITURA_CO_PROCEDIMENTO, MSG_ERRO_LEITURA_TAMANHO, MSG_INICIO_PROCESSAMENTO_MODALIDADE, 
		MSG_ERRO_LEITURA_CO_MODALIDADE, ARQUIVO_NAO_ENCONTRADO, ERRO_LER_COMPETENCIA, NOME_ARQUIVO_INVALIDO, MSG_COMPARANDO_DIFERENCAS,
		MSG_ERRO_LEITURA_PROCEDIMENTO, PROCESSANDO_REGISTROS_ARQUIVO, CARREGANDO, INCLUINDO, TABELA_LIDA_ATUALIZADA_SUCESSO, LEIAUTE_INCOMPATIVEL, 
		ERRO_ATUALIZACAO_TABELA, EXCLUINDO_REGISTROS_FAT_PROCED_HOSP_INT_CID, MSG_ERRO_LEITURA_LINHA_FORMA_ORGANIZACAO, MSG_ERRO_BUSCA_GRUPO,
		MSG_PARAMETROS_NAO_ENCONTRADOS, MSG_NAO_ENCONTROU_COMPETENCIA_ABERTA
	}
	//private static final int TEMPO = 60 * 60 * 24;

	@Asynchronous
	public void processarFinanciamentoComplexidade(final File file, final List<String> listaNomeArquivos) {
			final ControleProcessadorArquivosImportacaoSus controle = new ControleProcessadorArquivosImportacaoSus();
			controle.abrirArquivoLog(file);
			processadorArquivosImportacaoSusProcHospON.atualizarItensProcedimentoHospitalar(controle, listaNomeArquivos);
			controle.fechaArquivoLog();		
	}
	
	@Asynchronous
	public void processarGeral(final File file, final List<String> listaNomeArquivo) {
			
			final ControleProcessadorArquivosImportacaoSus controle = new ControleProcessadorArquivosImportacaoSus();
			
			controle.abrirArquivoLog(file);
	
			processadorArquivosImportacaoSusGruposON.atualizarGrupo(controle);
			
			processadorArquivosImportacaoSusSubGruposON.atualizarSubGrupo(controle);
			
			processadorArquivosImportacaoSusFormasOrganizacaoON.atualizarFormasOrganizacao(controle);

			processadorArquivosImportacaoSusModalidadeON.atualizarModalidade(controle);
			
			processadorArquivosImportacaoSusProcHospON.atualizarItensProcedimentoHospitalar(controle, listaNomeArquivo);

			processadorArquivosImportacaoSusCBOProcON.atualizarCboProcedimento(controle, listaNomeArquivo);
			
			processadorArquivosImportacaoSusCIDProcON.atualizarCidProcedimento(controle, listaNomeArquivo);

			
			controle.setNrRegistrosProcessadosGeral(controle.getNrRegistrosProcessados());
			controle.setNrRegistrosProcessoGeral(controle.getNrRegistrosProcesso());
			processadorArquivosImportacaoSusCompatibilidadeON.atualizarCompatibilidade(controle);

			controle.setNrRegistrosProcessadosGeral(controle.getNrRegistrosProcessados());
			controle.setNrRegistrosProcessoGeral(controle.getNrRegistrosProcesso());
			
//			try{
			processadorArquivosImportacaoSusProcedServicoON.atualizarProcedServico(controle);
//			} catch (Exception e){
//				//TODO o que fazer com e
//				super.rollbackTransaction();
//			}
			
			controle.fechaArquivoLog();		
	}
	
	@Asynchronous
	public void processarCboProcedimento(final File file, final List<String> listaNomeArquivos) {
		ControleProcessadorArquivosImportacaoSus controle = new ControleProcessadorArquivosImportacaoSus();
		controle.abrirArquivoLog(file);
		processadorArquivosImportacaoSusCBOProcON.atualizarCboProcedimento(controle, listaNomeArquivos);
		controle.fechaArquivoLog();		
	}
	
	@Asynchronous
	public void processarCompatibilidade(final File file) {
		ControleProcessadorArquivosImportacaoSus controle = new ControleProcessadorArquivosImportacaoSus();
		controle.abrirArquivoLog(file);
		processadorArquivosImportacaoSusCompatibilidadeON.atualizarCompatibilidade(controle);
		controle.fechaArquivoLog();		
	}

	@Asynchronous
	public void processarServicoClassificacao(final File file) {
		ControleProcessadorArquivosImportacaoSus controle = new ControleProcessadorArquivosImportacaoSus();
		controle.abrirArquivoLog(file);
		processadorArquivosImportacaoSusProcedServicoON.atualizarProcedServico(controle);
		controle.fechaArquivoLog();		
	}
	@Asynchronous
	public void processarInstrumentoRegistro(final File file) {
		ControleProcessadorArquivosImportacaoSus controle = new ControleProcessadorArquivosImportacaoSus();
		controle.abrirArquivoLog(file);
		this.processadorArquivosImportacaoSusProcedRegistroON.atualizarInstrumentoRegistro(controle);
		controle.fechaArquivoLog();		
	}

	@Asynchronous
	public void processarFinancimanto(final File file) {
		ControleProcessadorArquivosImportacaoSus controle = new ControleProcessadorArquivosImportacaoSus();
		controle.abrirArquivoLog(file);
		this.processadorArquivosImportacaoSusFinanciamentoON.atualizarFinanciamento(controle);
		controle.fechaArquivoLog();		
	}
	
	@Asynchronous
	public void processarCidProcedimentoNovo(final File file, final List<String> listaNomeArquivos) {
		ControleProcessadorArquivosImportacaoSus controle = new ControleProcessadorArquivosImportacaoSus();
		controle.abrirArquivoLog(file);
		processadorArquivosImportacaoSusCIDProcON.atualizarCidProcedimento(controle, listaNomeArquivos);
		controle.fechaArquivoLog();		
	}

	@Asynchronous
	public void processarItensProcedimentoHospitalar(final File file, final List<String> listaNomeArquivos) {
		ControleProcessadorArquivosImportacaoSus controle = new ControleProcessadorArquivosImportacaoSus();
		controle.abrirArquivoLog(file);
		processadorArquivosImportacaoSusProcHospON.atualizarItensProcedimentoHospitalar(controle, listaNomeArquivos);
		controle.fechaArquivoLog();		
	}

	protected ProcessadorArquivosImportacaoSusCBOProcON getProcessadorArquivosImportacaoSusCBOProcON() {
		return processadorArquivosImportacaoSusCBOProcON;
	}

	protected ProcessadorArquivosImportacaoSusCIDProcON getProcessadorArquivosImportacaoSusCIDProcON() {
		return processadorArquivosImportacaoSusCIDProcON;
	}

	protected ProcessadorArquivosImportacaoSusProcHospON getProcessadorArquivosImportacaoSusProcHospON() {
		return processadorArquivosImportacaoSusProcHospON;
	}

	protected ProcessadorArquivosImportacaoSusGruposON getProcessadorArquivosImportacaoSusGruposON() {
		return processadorArquivosImportacaoSusGruposON;
	}

	protected ProcessadorArquivosImportacaoSusSubGruposON getProcessadorArquivosImportacaoSusSubGruposON() {
		return processadorArquivosImportacaoSusSubGruposON;
	}

	protected ProcessadorArquivosImportacaoSusModalidadeON getProcessadorArquivosImportacaoSusModalidadeON() {
		return processadorArquivosImportacaoSusModalidadeON;
	}

	protected ProcessadorArquivosImportacaoSusFormasOrganizacaoON getProcessadorArquivosImportacaoSusFormasOrganizacaoON() {
		return processadorArquivosImportacaoSusFormasOrganizacaoON;
	}

	protected ProcessadorArquivosImportacaoSusCompatibilidadeON getProcessadorArquivosImportacaoSusCompatibilidadeON() {
		return processadorArquivosImportacaoSusCompatibilidadeON;
	}
	
	protected ProcessadorArquivosImportacaoSusProcedServicoON getProcessadorArquivosImportacaoSusProcedServicoON() {
		return processadorArquivosImportacaoSusProcedServicoON;
	}
	
	protected ProcessadorArquivosImportacaoSusFinanciamentoON getProcessadorArquivosImportacaoSusFinanciamentoON() {
		return processadorArquivosImportacaoSusFinanciamentoON;
	}
	protected ProcessadorArquivosImportacaoSusProcedRegistroON getProcessadorArquivosImportacaoSusProcedRegistroON() {
		return processadorArquivosImportacaoSusProcedRegistroON;
	}
	@Override
	protected Log getLogger() {
		return LOG;
	}
}