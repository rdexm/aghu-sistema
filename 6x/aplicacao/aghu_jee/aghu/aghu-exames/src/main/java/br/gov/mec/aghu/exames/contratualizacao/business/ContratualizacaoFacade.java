package br.gov.mec.aghu.exames.contratualizacao.business;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioAgrupamentoTotaisExames;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.exames.contratualizacao.util.Detalhes;
import br.gov.mec.aghu.exames.contratualizacao.util.Header;
import br.gov.mec.aghu.exames.contratualizacao.vo.TotalItemSolicitacaoExamesVO;
import br.gov.mec.aghu.exames.dao.AelArquivoIntegracaoDAO;
import br.gov.mec.aghu.exames.dao.AelArquivoSolicitacaoDAO;
import br.gov.mec.aghu.model.AelArquivoIntegracao;
import br.gov.mec.aghu.model.AelArquivoSolicitacao;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapServidores;




@Modulo(ModuloEnum.EXAMES_LAUDOS)
@Stateless
public class ContratualizacaoFacade extends BaseFacade implements IContratualizacaoFacade {


@EJB
private PesquisaSolicitacoesArquivoON pesquisaSolicitacoesArquivoON;

@EJB
private RelatorioTotaisExamesPorPeriodoON relatorioTotaisExamesPorPeriodoON;


@EJB
private ProcessadorArquivosContratualizacaoRN processadorArquivosContratualizacaoRN;


@EJB
private ReceberArquivoXmlON receberArquivoXmlON;

@Inject
private AelArquivoSolicitacaoDAO aelArquivoSolicitacaoDAO;

@Inject
private AelArquivoIntegracaoDAO aelArquivoIntegracaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3276841359585725115L;

	@Override
	@Secure("#{s:hasPermission('importarArquivoContratualizacao','executar')}")
	public void verificarFormatoNomeArquivoXml(String nomeXml) throws BaseException {
		getReceberArquivoXmlON().verificarFormatoArquivoXml(nomeXml);
	}	
	
	private ReceberArquivoXmlON getReceberArquivoXmlON() {
		return receberArquivoXmlON;
	}
	
	private PesquisaSolicitacoesArquivoON getPesquisaSolicitacoesArquivoON() {
		return pesquisaSolicitacoesArquivoON;
	}
	
	private AelArquivoSolicitacaoDAO getAelArquivoSolicitacaoDAO(){
		return aelArquivoSolicitacaoDAO;
	}
	
	@Override
	public Detalhes verificarEstruturaArquivoXml(String caminhoAbsolutoXml) throws BaseException {
		return getReceberArquivoXmlON().verificarEstruturaArquivoXml(caminhoAbsolutoXml);
	}

	@Override
	public Map<String, Object> importarArquivoContratualizacao(Detalhes detalhes, String caminhoAbsoluto, String nomeArquivo, Header headerIntegracao, String nomeMicrocomputador) throws BaseException {
		return getReceberArquivoXmlON().importarArquivoContratualizacao(detalhes, caminhoAbsoluto, nomeArquivo, headerIntegracao, nomeMicrocomputador);
	}

	@Override
	public void verificarPermissaoDeEscritaPastasDestino() throws BaseException {
		getReceberArquivoXmlON().verificarPermissaoDeEscritaPastasDestino();
	}
	
	@Override
	public List<AelArquivoIntegracao> pesquisarArquivosIntegracao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Date dataInicial, Date dataFinal, String nomeArquivo,
			boolean solicitacaoComErro, boolean solicitacaoSemAgenda) {
		return this.getAelArquivoIntegracaoDAO().pesquisarArquivos(firstResult,
				maxResult, orderProperty, asc, dataInicial, dataFinal,
				nomeArquivo, solicitacaoComErro, solicitacaoSemAgenda);
	}

	@Override
	public Long pesquisarArquivosIntegracaoCount(Date dataInicial,
			Date dataFinal, String nomeArquivo, boolean solicitacaoComErro,
			boolean solicitacaoSemAgenda) {
		return this.getAelArquivoIntegracaoDAO().pesquisarArquivosCount(
				dataInicial, dataFinal, nomeArquivo, solicitacaoComErro,
				solicitacaoSemAgenda);
	}	

	@Override
	public List<AelArquivoIntegracao> pesquisarArquivoIntegracaoPeloNome(Object param){
		return this.getAelArquivoIntegracaoDAO().pesquisarArquivoIntegracaoPeloNome(param);
	}
	
	
	private AelArquivoIntegracaoDAO getAelArquivoIntegracaoDAO(){
		return aelArquivoIntegracaoDAO;
	}	

	@Override
	public List<AelArquivoSolicitacao> pesquisarSolicitacoesPorArquivo(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AelArquivoIntegracao aelArquivoIntegracao,
			String nomePaciente, Integer numeroSolicitacao,
			boolean statusSucesso, boolean statusErro,
			boolean envioComAgendamento, boolean envioSemAgendamento) {
		return getAelArquivoSolicitacaoDAO().pesquisarSolicitacoesPorArquivo(
				firstResult, maxResult, orderProperty, asc,
				aelArquivoIntegracao, nomePaciente, numeroSolicitacao,
				statusSucesso, statusErro, envioComAgendamento,
				envioSemAgendamento);
	}
	
	@Override
	public AelArquivoIntegracao buscarArquivoIntegracaoPeloSeq(Integer seq){
		return this.getAelArquivoIntegracaoDAO().obterPorChavePrimaria(seq);
	}
	
	@Override
	public Long pesquisarSolicitacoesPorArquivoCount(
			AelArquivoIntegracao aelArquivoIntegracao, String nomePaciente,
			Integer numeroSolicitacao, boolean statusSucesso,
			boolean statusErro, boolean envioComAgendamento,
			boolean envioSemAgendamento) {
		return this.getAelArquivoSolicitacaoDAO()
				.pesquisarSolicitacoesPorArquivoCount(aelArquivoIntegracao,
						nomePaciente, numeroSolicitacao, statusSucesso,
						statusErro, envioComAgendamento, envioSemAgendamento);
	}
	
	@Override
	public void validarFiltrosPesquisa(
			AelArquivoIntegracao aelArquivoIntegracao, String nomePaciente,
			Integer numeroSolicitacao) throws ApplicationBusinessException {
		this.getPesquisaSolicitacoesArquivoON().validarFiltrosPesquisa(
				aelArquivoIntegracao, nomePaciente, numeroSolicitacao);
	}
	
	@Override
	public List<AelSitItemSolicitacoes> obterListaSituacoesItemSolicitacoes() {

		return this.getRelatorioTotaisExamesPorPeriodoON().obterListaSituacoesItemSolicitacoes();
	}

	@Override
	public List<FatConvenioSaudePlano> sbObterConvenio(Object parametro) {

		return this.getRelatorioTotaisExamesPorPeriodoON().sbObterConvenio(parametro);
	}
	
	@Override
	public Long sbObterConvenioCount(Object parametro) {

		return this.getRelatorioTotaisExamesPorPeriodoON().sbObterConvenioCount(parametro);
	}
	
	@Override
	public List<AelExames> sbObterExame(Object parametro) {

		return this.getRelatorioTotaisExamesPorPeriodoON().sbObterExame(parametro);
	}

	@Override
	public Long sbObterExameCount(Object parametro) {

		return this.getRelatorioTotaisExamesPorPeriodoON().sbObterExameCount(parametro);
	}
	
	@Override
	@Secure("#{s:hasPermission('gerarExamesIntegracao','inserir')}")
	public List<TotalItemSolicitacaoExamesVO> buscarTotais(Date dataInicial,
			Date dataFinal, Short convenio, Byte plano,
			DominioSituacaoItemSolicitacaoExame situacao, Short unfSeq,
			String sigla, DominioAgrupamentoTotaisExames tipoRelatorio) {

		return this.getRelatorioTotaisExamesPorPeriodoON().buscarTotais(
				dataInicial, dataFinal, convenio, plano, situacao, unfSeq,
				sigla, tipoRelatorio);
	}
	
	@Override
	public void processar(Detalhes detalhes, String nomeArquivo, String nomeArquivoOriginal, Header headerIntegracao, String caminhoAbsoluto, RapServidores servidorLogado, String nomeMicrocomputador) throws ApplicationBusinessException {
		getProcessadorArquivosContratualizacaoRN().processar(detalhes, nomeArquivo, nomeArquivoOriginal, headerIntegracao, caminhoAbsoluto, servidorLogado, nomeMicrocomputador);
	}

	
	@Override
	@Secure("#{s:hasPermission('gerarExamesIntegracao','inserir')}")
	public void gerarCSV(Date dataInicial, Date dataFinal, Short convenio,
			Byte plano, DominioSituacaoItemSolicitacaoExame situacao,
			Short unfSeq, String sigla,
			DominioAgrupamentoTotaisExames tipoRelatorio)
			throws ApplicationBusinessException, BaseException, IOException {
		this.getRelatorioTotaisExamesPorPeriodoON().gerarCSV(dataInicial,
				dataFinal, convenio, plano, situacao, unfSeq, sigla,
				tipoRelatorio);
	}

	private RelatorioTotaisExamesPorPeriodoON getRelatorioTotaisExamesPorPeriodoON() {
		return relatorioTotaisExamesPorPeriodoON;
	}

	private ProcessadorArquivosContratualizacaoRN getProcessadorArquivosContratualizacaoRN() {
		return processadorArquivosContratualizacaoRN;
	}

	
}

