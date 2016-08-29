package br.gov.mec.aghu.exames.contratualizacao.business;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import br.gov.mec.aghu.dominio.DominioAgrupamentoTotaisExames;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.exames.contratualizacao.util.Detalhes;
import br.gov.mec.aghu.exames.contratualizacao.util.Header;
import br.gov.mec.aghu.exames.contratualizacao.vo.TotalItemSolicitacaoExamesVO;
import br.gov.mec.aghu.model.AelArquivoIntegracao;
import br.gov.mec.aghu.model.AelArquivoSolicitacao;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public interface IContratualizacaoFacade extends Serializable {

	public void verificarFormatoNomeArquivoXml(String nomeXml)
			throws BaseException;

	public Detalhes verificarEstruturaArquivoXml(String caminhoAbsolutoXml)
			throws BaseException;

	public Map<String, Object> importarArquivoContratualizacao(
			Detalhes detalhes, String caminhoAbsoluto, String nomeArquivo,
			Header headerIntegracao, String nomeMicrocomputador) throws BaseException;

	public void verificarPermissaoDeEscritaPastasDestino()
			throws BaseException;

	public List<AelArquivoIntegracao> pesquisarArquivosIntegracao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Date dataInicial, Date dataFinal, String nomeArquivo,
			boolean solicitacaoComErro, boolean solicitacaoSemAgenda);

	public Long pesquisarArquivosIntegracaoCount(Date dataInicial,
			Date dataFinal, String nomeArquivo, boolean solicitacaoComErro,
			boolean solicitacaoSemAgenda);

	public List<AelArquivoIntegracao> pesquisarArquivoIntegracaoPeloNome(
			Object param);

	public List<AelArquivoSolicitacao> pesquisarSolicitacoesPorArquivo(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AelArquivoIntegracao aelArquivoIntegracao,
			String nomePaciente, Integer numeroSolicitacao,
			boolean statusSucesso, boolean statusErro,
			boolean envioComAgendamento, boolean envioSemAgendamento);

	public AelArquivoIntegracao buscarArquivoIntegracaoPeloSeq(Integer seq);

	public Long pesquisarSolicitacoesPorArquivoCount(
			AelArquivoIntegracao aelArquivoIntegracao, String nomePaciente,
			Integer numeroSolicitacao, boolean statusSucesso,
			boolean statusErro, boolean envioComAgendamento,
			boolean envioSemAgendamento);

	public void validarFiltrosPesquisa(
			AelArquivoIntegracao aelArquivoIntegracao, String nomePaciente,
			Integer numeroSolicitacao) throws ApplicationBusinessException;
	
	public List<AelSitItemSolicitacoes> obterListaSituacoesItemSolicitacoes();
	
	public List<FatConvenioSaudePlano> sbObterConvenio(Object parametro);
	
	public Long sbObterConvenioCount(Object parametro);
	
	public List<AelExames> sbObterExame(Object parametro);
	
	public Long sbObterExameCount(Object parametro);

	public List<TotalItemSolicitacaoExamesVO> buscarTotais(Date dataInicial,
			Date dataFinal, Short convenio, Byte plano,
			DominioSituacaoItemSolicitacaoExame situacao, Short unfSeq,
			String sigla, DominioAgrupamentoTotaisExames tipoRelatorio);
	
	public void gerarCSV(Date dataInicial, Date dataFinal, Short convenio,
			Byte plano, DominioSituacaoItemSolicitacaoExame situacao,
			Short unfSeq, String sigla,
			DominioAgrupamentoTotaisExames tipoRelatorio)
			throws ApplicationBusinessException, BaseException, IOException;

	void processar(Detalhes detalhes, String nomeArquivo,
			String nomeArquivoOriginal, Header headerIntegracao,
			String caminhoAbsoluto, RapServidores servidorLogado,
			String nomeMicrocomputador) throws ApplicationBusinessException;
	
}