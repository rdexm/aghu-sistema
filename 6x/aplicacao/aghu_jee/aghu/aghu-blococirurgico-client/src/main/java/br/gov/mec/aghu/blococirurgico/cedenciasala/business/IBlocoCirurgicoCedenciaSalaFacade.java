package br.gov.mec.aghu.blococirurgico.cedenciasala.business;

import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.blococirurgico.cedenciasala.vo.CedenciaSalaInstitucionalParaEquipeVO;
import br.gov.mec.aghu.blococirurgico.cedenciasala.vo.CedenciaSalasEntreEquipesEquipeVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioDiaSemanaSigla;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcBloqSalaCirurgica;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcCaractSalaEspId;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcCedenciaSalaHcpa;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcSubstEscalaSala;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;

import java.io.Serializable;


public interface IBlocoCirurgicoCedenciaSalaFacade extends Serializable {
	
	public Long pesquisarNomeMatVinCodUnfByVMbcProfServCount(Object pesquisaSuggestion, Short unfSeq,
			DominioSituacao situacao, DominioFuncaoProfissional... funcoesProfissionais) ;

	public List<LinhaReportVO> pesquisarNomeMatVinCodUnfByVMbcProfServ(
			Object pesquisaSuggestion, Short unfSeq, DominioSituacao situacao,
			DominioFuncaoProfissional... funcoesProfissionais);

	public List<MbcCedenciaSalaHcpa> pesquisarCedenciasProgramadas(
			MbcCedenciaSalaHcpa mbcCedenciaSala, LinhaReportVO equipe,
			Integer firstResult, Integer maxResult, String order, boolean asc);

	public Long pesquisarCedenciasProgramadasCount(
			MbcCedenciaSalaHcpa mbcCedenciaSala, LinhaReportVO equipe);

	void atualizarMbcBloqSalaCirurgica(MbcBloqSalaCirurgica bloqueioSalaCirurgica) throws ApplicationBusinessException;

	List<MbcBloqSalaCirurgica> pesquisarBloqSalaCirurgica(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, String unfSigla,
			Short seqp, Date dtInicio, Date dtFim, DominioDiaSemanaSigla diaSemana, String turno, Short vinCodigo, Integer matricula, Short especialidade);

	Long pesquisarBloqSalaCirurgicaCount(String unfSigla, Short seqp, Date dtInicio, Date dtFim, DominioDiaSemanaSigla diaSemana, String turno,
			Short vinCodigo, Integer matricula, Short especialidade);

	List<MbcTurnos> pesquisarTurnos(String objPesquisa);

	Long pesquisarTurnosCount(String objPesquisa);

	MbcBloqSalaCirurgica obterOriginalMbcBloqSalaCirurgica(MbcBloqSalaCirurgica bloqueioSalaCirurgica);	
	

	public void ativarInativarMbcCedenciaSalaHcpa(
			MbcCedenciaSalaHcpa mbcCedenciaSala) throws ApplicationBusinessException;

	Long pesquisarMbcCaracteristicaSalaCirgComCaracSalaEspCount(
			MbcCaracteristicaSalaCirg caractSalaFiltro,
			DominioSituacao sciSituacao, DominioSituacao casSituacao,
			DominioSituacao cseIndSituacao, Boolean casIndDisponivel);

	List<MbcCaracteristicaSalaCirg> pesquisarMbcCaracteristicaSalaCirgComCaracSalaEsp(
			MbcCaracteristicaSalaCirg caractSalaFiltro,
			DominioSituacao sciSituacao, DominioSituacao casSituacao,
			DominioSituacao cseIndSituacao, Boolean casIndDisponivel,
			Integer firstResult, Integer maxResult, boolean asc, String order);

	public MbcCaracteristicaSalaCirg obterMbcCaracteristicaSalaCirgPorChavePrimaria(
			Short mbcCaracteristicaSalaCirgSeq);

	public String gravarMbcBloqSalaCirurgica(MbcBloqSalaCirurgica bloqueioSalaCirurgica, LinhaReportVO equipe) throws ApplicationBusinessException;

	public void persistirMbcBloqSalaCirurgica(MbcBloqSalaCirurgica bloqueioSalaCirurgica);

	public List<LinhaReportVO> pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgs(Object pesquisaSuggestion, Short unfSeq, Short sciSeqp,
			DominioDiaSemanaSigla dominioDiaSemanaSigla, String turno, DominioSituacao situacao, DominioFuncaoProfissional... funcoesProfissionais);

	public List<LinhaReportVO> pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgs(Object pesquisaSuggestion, Short unfSeq, Short sciSeqp,
			DominioDiaSemanaSigla dominioDiaSemanaSigla, String turno, DominioSituacao situacao, boolean matriculaLong, DominioFuncaoProfissional... funcoesProfissionais);

	public Long pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgsCount(Object pesquisaSuggestion, Short unfSeq, Short sciSeqp,
			DominioDiaSemanaSigla dominioDiaSemanaSigla, String turno, DominioSituacao situacao, DominioFuncaoProfissional... funcoesProfissionais);

	public Long pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgsCount(Object pesquisaSuggestion, Short unfSeq, Short sciSeqp,
			DominioDiaSemanaSigla dominioDiaSemanaSigla, String turno, DominioSituacao situacao, boolean matriculaLong, DominioFuncaoProfissional... funcoesProfissionais);
	
	public Integer gravarCedenciaDeSalaInstitucionalParaEquipe(
			CedenciaSalaInstitucionalParaEquipeVO cedenciaSala,MbcCaracteristicaSalaCirg caracteristicaSalaCirg,  AghEspecialidades especialidade) throws ApplicationBusinessException;

	public List<MbcSubstEscalaSala> listarCedenciaSalasEntreEquipes(
			MbcCedenciaSalaHcpa mbcCedenciaSala, LinhaReportVO equipe,
			Integer firstResult, Integer maxResult, String order, boolean asc);

	public Long listarCedenciaSalasEntreEquipesCount(
			MbcCedenciaSalaHcpa mbcCedenciaSala, LinhaReportVO equipe);

	void ativarInativarMbcSubstEscalaSala(
			MbcSubstEscalaSala mbcSubstEscalaSala)
			throws ApplicationBusinessException;

	public MbcCaractSalaEsp obterMbcCaractSalaEspPorChavePrimaria(
			MbcCaractSalaEspId idMbcCaractSalaEsp);

	public Integer gravarMbcSubstEscalaSala(MbcCaractSalaEsp mbcCaractSalaEsp,
			CedenciaSalasEntreEquipesEquipeVO cedenciaSalasEntreEquipesEquipeVO) throws ApplicationBusinessException;

	public void verificarProgramacaoAgendaSala(MbcSubstEscalaSala mbcSubstEscalaSala) throws ApplicationBusinessException;

	public void verificarProgramacaoAgendaSalaInstitucional(MbcCedenciaSalaHcpa cedenciaSelecionada)throws ApplicationBusinessException;

	public List<MbcCaracteristicaSalaCirg> listarCaracteristicaSalaCirgPorDiaSemana(Object objPesquisa, Date data);

    public Long pesquisaCaracteristicaSalaCirgPorDiaSemanaCount(
            Object objPesquisa, Date data);

	public List<MbcTurnos> pesquisarTurnosPorUnidade(String param, Short unfSeq);

	public Long pesquisarTurnosPorUnidadeCount(String param, Short unfSeq);

	public List<MbcCaractSalaEsp> listarMbcCaractSalaEspPorDiaSemana(
			Object objPesquisa, Date data);

    public Long pesquisarMbcCaractSalaEspPorDiaSemanaCount(
            Object objPesquisa, Date data);
           
    public List<AghEspecialidades> pesquisarEspecialidadePorVbmcProfServidor(String pesquisa, Integer ser_matricula, Short ser_vin_codigo, Short ufseq);
    
    public Long pesquisarEspecialidadePorVbmcProfServidorCount(String pesquisa, Integer ser_matricula, Short ser_vin_codigo, Short ufseq);        

	public List<AghEspecialidades> pesquisarSiglaEspecialidadeSubstituta(String param,
			LinhaReportVO equipeSubstituta);

	public long pesquisarSiglaEspecialidadeSubstitutaCount(String param,
			LinhaReportVO equipeSubstituta);
    
    public List<AghEspecialidades> pesquisarCaractSalaEspAtivasProfAtuaUnidCirgs(MbcProfAtuaUnidCirgs mbc);
}
