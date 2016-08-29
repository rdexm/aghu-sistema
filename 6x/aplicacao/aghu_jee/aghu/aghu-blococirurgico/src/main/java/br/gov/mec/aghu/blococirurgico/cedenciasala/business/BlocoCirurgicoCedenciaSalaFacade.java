package br.gov.mec.aghu.blococirurgico.cedenciasala.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.cedenciasala.vo.CedenciaSalaInstitucionalParaEquipeVO;
import br.gov.mec.aghu.blococirurgico.cedenciasala.vo.CedenciaSalasEntreEquipesEquipeVO;
import br.gov.mec.aghu.blococirurgico.dao.MbcBloqSalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaractSalaEspDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaracteristicaSalaCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCedenciaSalaHcpaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSubstEscalaSalaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcTurnosDAO;
import br.gov.mec.aghu.blococirurgico.dao.VMbcProfServidorDAO;
import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesDAO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
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


@Modulo(ModuloEnum.BLOCO_CIRURGICO)
@Stateless
public class BlocoCirurgicoCedenciaSalaFacade extends BaseFacade implements IBlocoCirurgicoCedenciaSalaFacade{

	@Inject
	private MbcSubstEscalaSalaDAO mbcSubstEscalaSalaDAO;

	@Inject
	private MbcBloqSalaCirurgicaDAO mbcBloqSalaCirurgicaDAO;

	@Inject
	private MbcCaracteristicaSalaCirgDAO mbcCaracteristicaSalaCirgDAO;

	@Inject
	private VMbcProfServidorDAO vMbcProfServidorDAO;

	@Inject
	private MbcCaractSalaEspDAO mbcCaractSalaEspDAO;

	@Inject
	private MbcCedenciaSalaHcpaDAO mbcCedenciaSalaHcpaDAO;

	@Inject
	private MbcTurnosDAO mbcTurnosDAO;
	
	@Inject
	private AghEspecialidadesDAO aghEspecialidadesDAO;

	@EJB
	private CedenciaSalaInstitucionalParaEquipeON cedenciaSalaInstitucionalParaEquipeON;

	@EJB
	private BloqueioSalaON bloqueioSalaON;

	@EJB
	private CedenciaSalasEntreEquipeON cedenciaSalasEntreEquipeON;

	private static final long serialVersionUID = -5515264161472884029L;

	private BloqueioSalaON getBloqueioSalaON() { 
		return bloqueioSalaON;
	}

	@Override
	public void atualizarMbcBloqSalaCirurgica(MbcBloqSalaCirurgica bloqueioSalaCirurgica) throws ApplicationBusinessException {
		getBloqueioSalaON().atualizarMbcBloqSalaCirurgica(bloqueioSalaCirurgica);
	}

	@Override
	public List<MbcBloqSalaCirurgica> pesquisarBloqSalaCirurgica(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String unfSigla, Short seqp, Date dtInicio, Date dtFim, DominioDiaSemanaSigla diaSemana, String turno,
			Short vinCodigo, Integer matricula, Short especialidade) {
		return getBloqueioSalaON().pesquisarBloqSalaCirurgica(firstResult, maxResult, orderProperty, asc, 
				unfSigla, seqp, dtInicio, dtFim, diaSemana, turno, vinCodigo, matricula, especialidade);
	}

	@Override
	public Long pesquisarBloqSalaCirurgicaCount(String unfSigla, Short seqp, Date dtInicio, Date dtFim, DominioDiaSemanaSigla diaSemana,
			String turno, Short vinCodigo, Integer matricula, Short especialidade) {
		return getBloqueioSalaON().pesquisarBloqSalaCirurgicaCount(unfSigla, seqp, dtInicio, dtFim, diaSemana, turno, vinCodigo, matricula, especialidade);
	}

	@Override
	public List<MbcTurnos> pesquisarTurnos(String objPesquisa) {
		return getMbcTurnosDAO().pesquisarTurnos(objPesquisa);
	}

	@Override
	public Long pesquisarTurnosCount(String objPesquisa) {
		return getMbcTurnosDAO().pesquisarTurnosCount(objPesquisa);
	}
	
	private MbcTurnosDAO getMbcTurnosDAO() {
		return mbcTurnosDAO;
	}

	@Override
	public MbcBloqSalaCirurgica obterOriginalMbcBloqSalaCirurgica(MbcBloqSalaCirurgica bloqueioSalaCirurgica) {
		return getMbcBloqSalaCirurgicaDAO().obterOriginal(bloqueioSalaCirurgica);
	}	
	
	private MbcBloqSalaCirurgicaDAO getMbcBloqSalaCirurgicaDAO() {
		return mbcBloqSalaCirurgicaDAO;
	}


	@Override
	public List<LinhaReportVO> pesquisarNomeMatVinCodUnfByVMbcProfServ(
			Object pesquisaSuggestion, Short unfSeq, DominioSituacao situacao,
			DominioFuncaoProfissional... funcoesProfissionais) {
		return getVMbcProfServidorDAO().pesquisarNomeMatVinCodUnfByVMbcProfServ(pesquisaSuggestion,
						unfSeq, situacao, funcoesProfissionais);
	}

	@Override
	public Long pesquisarNomeMatVinCodUnfByVMbcProfServCount(
			Object pesquisaSuggestion, Short unfSeq, DominioSituacao situacao,
			DominioFuncaoProfissional... funcoesProfissionais) {
		return getVMbcProfServidorDAO()
				.pesquisarNomeMatVinCodUnfByVMbcProfServCount(
						pesquisaSuggestion, unfSeq, situacao,
						funcoesProfissionais);
	}
	
	@Override
	public List<AghEspecialidades> pesquisarEspecialidadePorVbmcProfServidor(String pesquisa, Integer ser_matricula, Short ser_vin_codigo, Short ufseq){
		return this.aghEspecialidadesDAO.pesquisarEspecialidadePorVbmcProfServidor(pesquisa, ser_matricula, ser_vin_codigo, ufseq);
	}
	
	@Override
	public Long pesquisarEspecialidadePorVbmcProfServidorCount(String pesquisa, Integer ser_matricula, Short ser_vin_codigo, Short ufseq ){
		return this.aghEspecialidadesDAO.pesquisarEspecialidadePorVbmcProfServidorCount(pesquisa, ser_matricula, ser_vin_codigo, ufseq);
	}
	
	protected VMbcProfServidorDAO getVMbcProfServidorDAO(){
		return vMbcProfServidorDAO;
	}
	
	private CedenciaSalaInstitucionalParaEquipeON getCedenciaSalaInstitucionalParaEquipeON(){
		return cedenciaSalaInstitucionalParaEquipeON;
	}
	
	private CedenciaSalasEntreEquipeON getCedenciaSalasEntreEquipeON(){
		return cedenciaSalasEntreEquipeON;
	}

	@Override
	public List<MbcCedenciaSalaHcpa> pesquisarCedenciasProgramadas(
			MbcCedenciaSalaHcpa mbcCedenciaSala, LinhaReportVO equipe,
			Integer firstResult, Integer maxResult, String order, boolean asc) {
		return getMbcCedenciaSalaHcpaDAO().pesquisarCedenciasProgramadas(
				mbcCedenciaSala, equipe, firstResult, maxResult, asc, order);
	}

	@Override
	public Long pesquisarCedenciasProgramadasCount(
			MbcCedenciaSalaHcpa mbcCedenciaSala, LinhaReportVO equipe) {
		return getMbcCedenciaSalaHcpaDAO().pesquisarCedenciasProgramadasCount(mbcCedenciaSala, equipe);
	}
	
	private MbcCedenciaSalaHcpaDAO getMbcCedenciaSalaHcpaDAO(){
		return mbcCedenciaSalaHcpaDAO;
	}

	@Override
	public void ativarInativarMbcCedenciaSalaHcpa(
			MbcCedenciaSalaHcpa mbcCedenciaSala) throws ApplicationBusinessException {
		getCedenciaSalaInstitucionalParaEquipeON().ativarInativarMbcCedenciaSalaHcpa(mbcCedenciaSala);
	}
	
	@Override
	public void ativarInativarMbcSubstEscalaSala(
			MbcSubstEscalaSala mbcSubstEscalaSala) throws ApplicationBusinessException {
		getCedenciaSalasEntreEquipeON().atualizarMbcSubstEscalaSala(mbcSubstEscalaSala);
	}
	
	@Override
	public List<MbcCaracteristicaSalaCirg> pesquisarMbcCaracteristicaSalaCirgComCaracSalaEsp(
			MbcCaracteristicaSalaCirg caractSalaFiltro,
			DominioSituacao sciSituacao, DominioSituacao casSituacao,
			DominioSituacao cseIndSituacao, Boolean casIndDisponivel,
			Integer firstResult, Integer maxResult, boolean asc, String order) {
		return getMbcCaracteristicaSalaCirgDAO()
				.pesquisarMbcCaracteristicaSalaCirgComCaracSalaEsp(
						caractSalaFiltro, sciSituacao, casSituacao,
						cseIndSituacao, casIndDisponivel, firstResult,
						maxResult, asc, order);
	}
	
	@Override
	public Long pesquisarMbcCaracteristicaSalaCirgComCaracSalaEspCount(
			MbcCaracteristicaSalaCirg caractSalaFiltro,
			DominioSituacao sciSituacao, DominioSituacao casSituacao,
			DominioSituacao cseIndSituacao, Boolean casIndDisponivel) {
		return getMbcCaracteristicaSalaCirgDAO()
				.pesquisarMbcCaracteristicaSalaCirgComCaracSalaEspCount(
						caractSalaFiltro, sciSituacao, casSituacao,
						cseIndSituacao, casIndDisponivel);
	}
	
	protected MbcCaracteristicaSalaCirgDAO getMbcCaracteristicaSalaCirgDAO(){
		return mbcCaracteristicaSalaCirgDAO;
	}
	
	protected MbcSubstEscalaSalaDAO getMbcSubstEscalaSalaDAO(){
		return mbcSubstEscalaSalaDAO;
	}
	
	protected MbcCaractSalaEspDAO getMbcCaractSalaEspDAO(){
		return mbcCaractSalaEspDAO;
	}

	@Override
	public MbcCaracteristicaSalaCirg obterMbcCaracteristicaSalaCirgPorChavePrimaria(
			Short mbcCaracteristicaSalaCirgSeq) {
		return getMbcCaracteristicaSalaCirgDAO().obterPorChavePrimaria(mbcCaracteristicaSalaCirgSeq);
	}

	@Override
	public String gravarMbcBloqSalaCirurgica(MbcBloqSalaCirurgica bloqueioSalaCirurgica, LinhaReportVO equipe) throws ApplicationBusinessException {
		return getBloqueioSalaON().gravarMbcBloqSalaCirurgica(bloqueioSalaCirurgica, equipe);
	}

	@Override
	public void persistirMbcBloqSalaCirurgica(MbcBloqSalaCirurgica bloqueioSalaCirurgica) {
		getMbcBloqSalaCirurgicaDAO().persistir(bloqueioSalaCirurgica);			
	}

	@Override
	public List<LinhaReportVO> pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgs(Object pesquisaSuggestion, Short unfSeq, Short sciSeqp,
			DominioDiaSemanaSigla dominioDiaSemanaSigla, String turno, DominioSituacao situacao, DominioFuncaoProfissional... funcoesProfissionais) {
		return pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgs(pesquisaSuggestion, unfSeq, sciSeqp, dominioDiaSemanaSigla, turno, situacao, true, funcoesProfissionais);
	}

	@Override
	public List<LinhaReportVO> pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgs(Object pesquisaSuggestion, Short unfSeq, Short sciSeqp,
			DominioDiaSemanaSigla dominioDiaSemanaSigla, String turno, DominioSituacao situacao, boolean matriculaLong, DominioFuncaoProfissional... funcoesProfissionais) {
		return getBloqueioSalaON().pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgs(pesquisaSuggestion, unfSeq, sciSeqp,
				dominioDiaSemanaSigla, turno, situacao, funcoesProfissionais, matriculaLong);
	}

	@Override
	public Long pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgsCount(Object pesquisaSuggestion, Short unfSeq, Short sciSeqp,
			DominioDiaSemanaSigla dominioDiaSemanaSigla, String turno, DominioSituacao situacao, DominioFuncaoProfissional... funcoesProfissionais) {
		return pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgsCount(pesquisaSuggestion, unfSeq, sciSeqp, dominioDiaSemanaSigla, turno, situacao, true, funcoesProfissionais); 
	}

	@Override
	public Long pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgsCount(Object pesquisaSuggestion, Short unfSeq, Short sciSeqp,
			DominioDiaSemanaSigla dominioDiaSemanaSigla, String turno, DominioSituacao situacao, boolean matriculaLong, DominioFuncaoProfissional... funcoesProfissionais) {
		return getBloqueioSalaON().pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgsCount(pesquisaSuggestion, unfSeq, sciSeqp,
				dominioDiaSemanaSigla, turno, situacao, funcoesProfissionais, matriculaLong); 
	}

	@Override
	public Integer gravarCedenciaDeSalaInstitucionalParaEquipe(
			CedenciaSalaInstitucionalParaEquipeVO cedenciaSala,MbcCaracteristicaSalaCirg caracteristicaSalaCirg, AghEspecialidades especialidade) throws ApplicationBusinessException {
		return getCedenciaSalaInstitucionalParaEquipeON().gravarCedenciaDeSalaInstitucionalParaEquipe(cedenciaSala,caracteristicaSalaCirg, especialidade);
	}

	@Override
	public List<MbcSubstEscalaSala> listarCedenciaSalasEntreEquipes(
			MbcCedenciaSalaHcpa mbcCedenciaSala, LinhaReportVO equipe,
			Integer firstResult, Integer maxResult, String order, boolean asc) {
		return getMbcSubstEscalaSalaDAO().listarCedenciaSalasEntreEquipes(mbcCedenciaSala, equipe, firstResult, maxResult, order, asc);
	}
	
	@Override
	public Long listarCedenciaSalasEntreEquipesCount(
			MbcCedenciaSalaHcpa mbcCedenciaSala, LinhaReportVO equipe) {
		return getMbcSubstEscalaSalaDAO().listarCedenciaSalasEntreEquipesCount(mbcCedenciaSala, equipe);
	}

	@Override
	public MbcCaractSalaEsp obterMbcCaractSalaEspPorChavePrimaria(MbcCaractSalaEspId idMbcCaractSalaEsp) {
		return getMbcCaractSalaEspDAO().obterPorChavePrimaria(idMbcCaractSalaEsp);
	}

	@Override
	public Integer gravarMbcSubstEscalaSala(MbcCaractSalaEsp mbcCaractSalaEsp,
			CedenciaSalasEntreEquipesEquipeVO cedenciaSalasEntreEquipesEquipeVO) throws ApplicationBusinessException {
		return getCedenciaSalasEntreEquipeON().gravarMbcSubstEscalaSala(mbcCaractSalaEsp, cedenciaSalasEntreEquipesEquipeVO);
	}

	@Override
	public void verificarProgramacaoAgendaSala(MbcSubstEscalaSala mbcSubstEscalaSala) throws ApplicationBusinessException {
		getCedenciaSalasEntreEquipeON().verificarProgramacaoAgendaSala(mbcSubstEscalaSala);
		
	}

	@Override
	public void verificarProgramacaoAgendaSalaInstitucional(MbcCedenciaSalaHcpa cedenciaSelecionada) throws ApplicationBusinessException{
		getCedenciaSalaInstitucionalParaEquipeON().verificarProgramacaoAgendaSalaInstitucional(cedenciaSelecionada);		
	}

	@Override
	public List<MbcCaracteristicaSalaCirg> listarCaracteristicaSalaCirgPorDiaSemana(Object objPesquisa, Date data) {
		return getCedenciaSalaInstitucionalParaEquipeON().listarCaracteristicaSalaCirgPorDiaSemana(objPesquisa, data);
	}

    public Long pesquisaCaracteristicaSalaCirgPorDiaSemanaCount(Object objPesquisa, Date data) {
        return getCedenciaSalaInstitucionalParaEquipeON().pesquisaCaracteristicaSalaCirgPorDiaSemanaCount(objPesquisa, data);
    }

	@Override
	public List<MbcTurnos> pesquisarTurnosPorUnidade(String param, Short unfSeq) {
		return getMbcTurnosDAO().pesquisarTurnosPorUnidade(param, unfSeq);
	}

	@Override
	public Long pesquisarTurnosPorUnidadeCount(String param, Short unfSeq) {
		return getMbcTurnosDAO().pesquisarTurnosPorUnidadeCount(param, unfSeq);
	}

	@Override
	public List<MbcCaractSalaEsp> listarMbcCaractSalaEspPorDiaSemana(
			Object objPesquisa, Date data) {
		return getCedenciaSalasEntreEquipeON().listarMbcCaractSalaEspPorDiaSemana(objPesquisa, data);
	}

    public Long pesquisarMbcCaractSalaEspPorDiaSemanaCount(
            Object objPesquisa, Date data){
        return getCedenciaSalasEntreEquipeON().pesquisarMbcCaractSalaEspPorDiaSemanaCount(objPesquisa, data);
    }
    
    @Override
    public List<AghEspecialidades> pesquisarSiglaEspecialidadeSubstituta(String param, LinhaReportVO equipeSubstituta){
    	return getVMbcProfServidorDAO().pesquisarSiglaEspecialidadeSubstituta(param, equipeSubstituta);
    }
    
    @Override
    public long pesquisarSiglaEspecialidadeSubstitutaCount(String param, LinhaReportVO equipeSubstituta){
    	return getVMbcProfServidorDAO().pesquisarSiglaEspecialidadeSubstitutaCount(param, equipeSubstituta);
    }
    
    public List<AghEspecialidades> pesquisarCaractSalaEspAtivasProfAtuaUnidCirgs(MbcProfAtuaUnidCirgs mbc){
    	return mbcCaractSalaEspDAO.pesquisarCaractSalaEspAtivasProfAtuaUnidCirgs(mbc);
    }
}
