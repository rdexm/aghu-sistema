package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.vo.AghUnidadesFuncionaisVO;
import br.gov.mec.aghu.model.AfaViaAdmUnf;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ManterViasAdmPermUndON extends BaseBusiness {


@EJB
private AfaViaAdmUnfRN afaViaAdmUnfRN;

@EJB
private IAghuFacade aghuFacade;

private static final Log LOG = LogFactory.getLog(ManterViasAdmPermUndON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 529533108603264531L;

	public void gravarViaAdmUnidade(AfaViaAdmUnf afaViaAdmUnf, Boolean edita) throws ApplicationBusinessException{
		if (edita){
			getAfaViaAdmUnfRN().update(afaViaAdmUnf);
		}else{
			getAfaViaAdmUnfRN().insert(afaViaAdmUnf);
		}
	}
	
	public List<AghUnidadesFuncionaisVO> obterUnidadesExecutora(Object paramPesquisa) {
		List<AghUnidadesFuncionaisVO> retorno = new ArrayList<AghUnidadesFuncionaisVO>();
		List<AghUnidadesFuncionais> unfs = getAghuFacade().listarUnidadeExecutora(paramPesquisa);
		if (unfs != null && !unfs.isEmpty()) {
			for (AghUnidadesFuncionais unf : unfs) {
				AghUnidadesFuncionaisVO vo = this.montaVO(unf);
				retorno.add(vo);
			}
		}
		return retorno;
	}
	
	public AghUnidadesFuncionaisVO obterUnidadeFuncionalVO(Short seq) {
		AghUnidadesFuncionais unf = getAghuFacade().obterUnidadeFuncional(seq);
		if (unf != null) {
			AghUnidadesFuncionaisVO vo = this.montaVO(unf);
			
			return vo;
		}
		return null;
	}
	
	private AghUnidadesFuncionaisVO montaVO(AghUnidadesFuncionais unf) {
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
		
		return vo;
	}
	
	protected AfaViaAdmUnfRN getAfaViaAdmUnfRN(){
		return afaViaAdmUnfRN;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
}
