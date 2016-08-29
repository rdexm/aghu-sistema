package br.gov.mec.aghu.exames.coleta.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.exames.agendamento.vo.ExamesAgendadosNoHorarioVO;
import br.gov.mec.aghu.exames.agendamento.vo.ItemHorarioAgendadoVO;
import br.gov.mec.aghu.exames.agendamento.vo.VAelHrGradeDispVO;
import br.gov.mec.aghu.exames.coleta.vo.AelExamesAmostraVO;
import br.gov.mec.aghu.exames.coleta.vo.AgendaExamesHorariosVO;
import br.gov.mec.aghu.exames.coleta.vo.GrupoExameVO;
import br.gov.mec.aghu.exames.coleta.vo.OrigemUnidadeVO;
import br.gov.mec.aghu.exames.coleta.vo.PesquisaSolicitacaoDiversosFiltroVO;
import br.gov.mec.aghu.exames.coleta.vo.RelatorioExameColetaPorUnidadeVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesResultsVO;
import br.gov.mec.aghu.exames.pesquisa.vo.VAelExamesAtdDiversosVO;
import br.gov.mec.aghu.exames.vo.AelAmostraExamesVO;
import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;
import br.gov.mec.aghu.model.AelAmostraItemExamesId;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelExtratoAmostras;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelInformacaoColeta;
import br.gov.mec.aghu.model.AelInformacaoColetaHist;
import br.gov.mec.aghu.model.AelInformacaoColetaHistId;
import br.gov.mec.aghu.model.AelInformacaoColetaId;
import br.gov.mec.aghu.model.AelInformacaoMdtoColeta;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public interface IColetaExamesFacade extends Serializable {

	public List<AgendaExamesHorariosVO> pesquisarAgendaExamesHorarios(Date dtAgenda, DominioSituacaoHorario situacaoHorario, Short unfSeq, Short seqP, RapServidoresId rapServidoresId);
	
	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExamePorSolicitacaoExame(Integer soeSeq);
	
	public List<ExamesAgendadosNoHorarioVO> pesquisarExamesAgendadosNoHorario(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda);
	
	public void validarAmostrasExamesAgendados(AelItemHorarioAgendado itemHorarioAgendado) throws ApplicationBusinessException;
	
	public void receberItemSolicitacaoExameAgendado(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda, DominioSituacaoHorario dominioSituacaoHorario, String nomeMicrocomputador) throws BaseException;
	
	public void voltarItemSolicitacaoExameAgendado(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda, DominioSituacaoHorario dominioSituacaoHorario, String nomeMicrocomputador) throws BaseException;

	void refresh(AelItemSolicitacaoExames itemSolicitacaoExames);
	
	public Boolean verificarMaterialAnaliseColetavel(AelItemHorarioAgendado	itemHorarioAgendado);	
	
	public AelInformacaoColeta persistirInformacaoColeta(AelInformacaoColeta informacaoColetaNew, 
			List<AelInformacaoMdtoColeta> listaInformacaoMdtoColetaNew, List<AelSolicitacaoExames> listaSolicitacaoExames) 
			throws BaseException;
	
	public void executarAtualizacaoColetaAmostraHorarioAgendado(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda, String nomeMicrocomputador)	
			throws BaseException;
	
	public AelInformacaoColeta obterInformacaoColeta(Integer soeSeq);
	
	public List<ItemHorarioAgendadoVO> pesquisarExamesParaTransferencia(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda);

	public List<VAelSolicAtendsVO> obterSolicAtendsPorItemHorarioAgendado(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda) throws ApplicationBusinessException;
	
	public List<AelAmostras> buscarAmostrasPorSolicitacaoExame(Integer soeSeq);
	
	public List<AelAmostras> buscarAmostrasPorAmostraESolicitacaoExame(Short amostraSeq, Integer soeSeq);
	
	public AelExtratoAmostras pesquisarExtratoAmostraAnterior(Integer soeSeq, Short seqp);
	
	public List<AelExamesAmostraVO> obterAmostraItemExamesPorAmostra(Integer soeSeq, Short seqp);
	
	public Boolean verificaSituacaoAmostraGeradaOuEmColeta(DominioSituacaoAmostra situacaoAmostra);
	
	public Boolean verificaSituacaoAmostraCURMA(DominioSituacaoAmostra situacaoAmostra);
	
	public void voltarColeta(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda, DominioSituacaoHorario situacao, String nomeMicrocomputador) throws BaseException;
	
	public OrigemUnidadeVO obterOrigemUnidadeSolicitante(Short gaeUnfSeq, Integer gaeSeqp, Date dthrAgenda);
	
	public List<ItemHorarioAgendadoVO> obterExamesSelecionados(List<ItemHorarioAgendadoVO> listaExamesAgendados);
	
	public List<VAelHrGradeDispVO> pesquisarHorariosParaExameSelecionado(Date dataHoraReativacao, Short tipo1, Short tipo2,ItemHorarioAgendadoVO itemHorarioAgendadoVO, Date data, Date hora, Integer grade, AelGrupoExames grupoExame, AelSalasExecutorasExames salaExecutoraExame, RapServidores servidor);
	
	public List<VAelHrGradeDispVO> pesquisarHorariosParaGrupoExameSelecionado(Date dataHoraReativacao, Short tipo1, Short tipo2, List<GrupoExameVO> listaGrupoExame, Date data, Date hora, Integer grade, AelGrupoExames grupoExame, AelSalasExecutorasExames salaExecutoraExame, RapServidores servidor);
	
	
	public void removerInformacaoMdtoColeta(AelInformacaoMdtoColeta informacaoMdtoColeta) throws ApplicationBusinessException;
	
	public Date obterDataReativacaoUnfExecutaExameAtiva(String emaExaSigla, Integer emaManSeq, Short unfSeq);
	
	public List<GrupoExameVO> pesquisarGrupoExameTransferenciaAgendamento(List<ItemHorarioAgendadoVO> listaItens);
	
	public Date obterMaiorDataReativacao(List<GrupoExameVO> listaItens);

	public void transferirHorarioAgendado(AelItemHorarioAgendado itemHorarioAgendado, Boolean permiteHoraExtra, AelUnfExecutaExames unfExecutoraExame, String nomeMicrocomputador) throws BaseException;
	
	public void validarSolicitacaoPorAmostra(Integer soeSeq, Short amostraSeq) throws ApplicationBusinessException;

	public List<VAelSolicAtendsVO> pesquisarSolicitacaoPorPaciente(Integer soeq, Integer pacCodigo);
	
	public List<VAelSolicAtendsVO> pesquisarSolicitacaoPorPacienteEAmostra(Integer soeq, Integer pacCodigo, Short amostraSeq);
	
	public void validarSolicitacaoPaciente(Integer soeSeq, Integer pacCodigo) throws ApplicationBusinessException;
	
	public AelAmostras obterAmostra(Integer soeSeq, Short seqp); 
	
	public void coletarExame(AelAmostraItemExamesId amostraItemExamesId, String nomeMicrocomputador) throws BaseException;
	
	public void voltarExame(AelAmostraItemExamesId amostraItemExamesId, String nomeMicrocomputador) throws BaseException;
	
	public void validarColetaExames(Integer amoSeqp, List<AelExamesAmostraVO> listaExamesAmostra) throws ApplicationBusinessException;
	
	public void validarVoltaExames(Integer amoSeqp, List<AelExamesAmostraVO> listaExamesAmostra) throws ApplicationBusinessException;
	
	public void validarColetaExame(List<AelAmostraExamesVO> listaItensAmostra) throws ApplicationBusinessException;
	
	public void validarVoltaExame(List<AelAmostraExamesVO> listaItensAmostra) throws ApplicationBusinessException;

	void atualizarSituacaoExamesAmostraColetada(AelAmostras amostra, String nomeMicrocomputador) throws BaseException;
	
	void atualizarSituacaoExamesAmostra(AelAmostras amostra, String nomeMicrocomputador) throws BaseException;
	
	public void validarTransferenciaAgendamento(DominioSituacaoHorario situacao) throws BaseException;
	
	public List<RelatorioExameColetaPorUnidadeVO> obterExamesColetaPorUnidade(AghUnidadesFuncionais unidadeExecutora) throws BaseException;

	public List<AelInformacaoMdtoColeta> buscarAelInformacaoMdtoColetaByAelInformacaoColeta(Short seqp, Integer soeSeq);

	public List<VAelExamesAtdDiversosVO> pesquisarSolicitacaoDiversos(PesquisaSolicitacaoDiversosFiltroVO filtro) throws ApplicationBusinessException;
	
	public List<AelSitItemSolicitacoes> pesquisarSitItemSolicitacoesPorCodigoOuDescricao(String parametro);

	public List<PesquisaExamesPacientesResultsVO> popularListaImpressaoLaudo(List<VAelExamesAtdDiversosVO> listaSolicitacaoDiversos);

	AelInformacaoColeta obterInformacaoColetaMascara(
			AelInformacaoColetaId informacaoColetaId);

	AelInformacaoColetaHist obterInformacaoColetaMascaraHist(
			AelInformacaoColetaHistId informacaoColetaId);
	
}