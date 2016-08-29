package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.vo.AghUnidadesFuncionaisVO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmHorarioInicAprazamento;
import br.gov.mec.aghu.model.MpmHorarioInicAprazamentoId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.prescricaomedica.dao.MpmHorarioInicAprazamentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class ManterHorarioInicAprazamentoON extends BaseBusiness {


@EJB
private ManterHorarioInicAprazamentoRN manterHorarioInicAprazamentoRN;

private static final Log LOG = LogFactory.getLog(ManterHorarioInicAprazamentoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmHorarioInicAprazamentoDAO mpmHorarioInicAprazamentoDAO;

@Inject
private MpmTipoFrequenciaAprazamentoDAO mpmTipoFrequenciaAprazamentoDAO;

@EJB
private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -241960240412478303L;

	protected MpmHorarioInicAprazamentoDAO getMpmHorarioInicAprazamentoDAO(){
		return mpmHorarioInicAprazamentoDAO;
	}

	protected MpmTipoFrequenciaAprazamentoDAO getMpmTipoFrequenciaAprazamentoDAO(){
		return mpmTipoFrequenciaAprazamentoDAO;
	}
	
	protected ManterHorarioInicAprazamentoRN getManterHorarioInicAprazamentoRN(){		
		return manterHorarioInicAprazamentoRN;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	/**
	 * Efetua a busca de horários de início de aprazamento
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param unfSeq
	 * @param situacao
	 * @return Lista de horários de início de aprazamento
	 */
	public List<MpmHorarioInicAprazamento> pesquisarHorariosInicioAprazamentos(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short unfSeq, DominioSituacao situacao) {		
		return getMpmHorarioInicAprazamentoDAO().pesquisarHorariosInicioAprazamentos(firstResult, maxResult, orderProperty,
				asc, unfSeq, situacao);		
	}
	
	/**
	 * Efetua o count para a busca de horários de início de aprazamento
	 * @param unfSeq
	 * @param situacao
	 * @return Count
	 */
	public Long pesquisarHorariosInicioAprazamentosCount(Short unfSeq, DominioSituacao situacao) {
		return getMpmHorarioInicAprazamentoDAO().pesquisarHorariosInicioAprazamentosCount(unfSeq, situacao);
	}

	/**
	 * Obtém unidades funcionais relativas à aprazamento por código ou descricao e situação
	 * @param paramPesquisa Código ou descrição
	 * @return Lista de Unidades Funcionais
	 */
	public List<AghUnidadesFuncionaisVO> pesquisarUnidadesFuncionaisAprazamentoPorCodigoDescricao(
			Object paramPesquisa) {
		Boolean ordenaPorDescricao = true;
		//Se a busca for pelo código, ordena por ele
		if(CoreUtil.isNumeroByte(paramPesquisa) || CoreUtil.isNumeroInteger(paramPesquisa) || CoreUtil.isNumeroLong(paramPesquisa) || CoreUtil.isNumeroShort(paramPesquisa)){
			ordenaPorDescricao = false;
		}
		List<AghUnidadesFuncionaisVO> retorno = new ArrayList<AghUnidadesFuncionaisVO>();
		List<AghUnidadesFuncionais> unfs = getAghuFacade().pesquisarUnidadesPorCodigoDescricao(paramPesquisa, ordenaPorDescricao);
		if (unfs != null && !unfs.isEmpty()) {
			for (AghUnidadesFuncionais unf : unfs) {
				AghUnidadesFuncionaisVO vo = new AghUnidadesFuncionaisVO();
				vo.setAndar(unf.getAndar());
				vo.setAndarAlaDescricao(unf.getAndarAlaDescricao());
				vo.setCapacInternacao(unf.getCapacInternacao());
				vo.setClinicas(unf.getClinica());
				vo.setDescricao(unf.getDescricao());
				vo.setDthrConfCenso(unf.getDthrConfCenso());
				vo.setHrioValidadePme(unf.getHrioValidadePme());
				vo.setIndAla(unf.getIndAla());
				vo.setIndBloqLtoIsolamento(unf.getIndBloqLtoIsolamento());
				vo.setIndConsClin(unf.getIndConsClin());
				vo.setIndPermPacienteExtra(unf.getIndPermPacienteExtra());
				vo.setIndSitUnidFunc(unf.getIndSitUnidFunc());
				vo.setIndUnidCti(unf.getIndUnidCti());
				vo.setIndUnidEmergencia(unf.getIndUnidEmergencia());
				vo.setIndUnidHospDia(unf.getIndUnidHospDia());
				vo.setIndUnidInternacao(unf.getIndUnidInternacao());
				vo.setIndVerfEscalaProfInt(unf.getIndVerfEscalaProfInt());
				vo.setNroViasPen(unf.getNroViasPen());
				vo.setNroViasPme(vo.getNroViasPme());
				vo.setSeq(unf.getSeq());
				vo.setTiposUnidadeFuncional(vo.getTiposUnidadeFuncional());
				retorno.add(vo);
			}
		}
		return retorno;
	}

	/**
	 * Inclui ou atualiza o o horário de início de aprazamento
	 * @param horarioAprazamento
	 */
	public void persistirHorarioInicioAprazamento(
			MpmHorarioInicAprazamento horarioAprazamento) throws BaseException  {
		Boolean inclusao = null;
		ManterHorarioInicAprazamentoRN manterHorarioInicAprazamentoRN = getManterHorarioInicAprazamentoRN();
		
		preencherRelacoesChaveCompostaHorarioAprazamento(horarioAprazamento);
		
		if(horarioAprazamento.getCriadoEm()==null){
			inclusao = Boolean.TRUE;
		}else{
			inclusao = Boolean.FALSE;
		}

		if(inclusao){
			manterHorarioInicAprazamentoRN.inserirHorarioInicioAprazamento(horarioAprazamento);
		}else{
			manterHorarioInicAprazamentoRN.atualizarHorarioInicioAprazamento(horarioAprazamento);			
		}
	}

	/**
	 * Método para preencher relações insertable e updatable false de chave composta
	 * para facilitar validações pré-inclusão.
	 * @param horarioAprazamento
	 */
	private void preencherRelacoesChaveCompostaHorarioAprazamento(MpmHorarioInicAprazamento horarioAprazamento){
		AghUnidadesFuncionais unidadeFuncionalSessao = null;
		MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = null;
		if(horarioAprazamento.getId() != null && horarioAprazamento.getId().getUnfSeq() != null){
			unidadeFuncionalSessao = getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(
					horarioAprazamento.getId().getUnfSeq());
			horarioAprazamento.setUnidadeFuncional(unidadeFuncionalSessao);
		}
		if(horarioAprazamento.getId() != null && horarioAprazamento.getId().getTfqSeq() != null){
			tipoFrequenciaAprazamento = getMpmTipoFrequenciaAprazamentoDAO().
					obterPorChavePrimaria(horarioAprazamento.getId().getTfqSeq());					
			horarioAprazamento.setTipoFreqAprazamento(tipoFrequenciaAprazamento);
		}
	}
	
	/**
	 * Efetua a exclusão do horário de aprazamento.
	 * @param horarioAprazamento
	 */
	public void removerHorarioAprazamento(
			MpmHorarioInicAprazamentoId horarioAprazamentoId) {
		MpmHorarioInicAprazamento horarioAprazamento = getMpmHorarioInicAprazamentoDAO().obterPorChavePrimaria(horarioAprazamentoId);
		getMpmHorarioInicAprazamentoDAO().remover(horarioAprazamento);
		getMpmHorarioInicAprazamentoDAO().flush();
		getManterHorarioInicAprazamentoRN().inserirHorarioAprazamentJnAposExclusaoHorarioInicAprazamento(horarioAprazamento);		
	}
}
