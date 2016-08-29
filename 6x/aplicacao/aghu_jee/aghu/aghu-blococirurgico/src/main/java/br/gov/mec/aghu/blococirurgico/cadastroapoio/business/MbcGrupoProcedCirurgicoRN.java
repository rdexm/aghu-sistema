package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcGrupoProcedCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoPorGrupoDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcGrupoProcedCirurgico;
import br.gov.mec.aghu.model.MbcProcedimentoPorGrupo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MbcGrupoProcedCirurgicoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcGrupoProcedCirurgicoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcedimentoPorGrupoDAO mbcProcedimentoPorGrupoDAO;

	@Inject
	private MbcGrupoProcedCirurgicoDAO mbcGrupoProcedCirurgicoDAO;


	private static final long serialVersionUID = -6320806656607859532L;
	
	private enum MbcGrupoProcedCirurgicoRNExceptionCode implements BusinessExceptionCode {
		MBC_GRUPO_PROCED_CIRURGICO_POSSUI_FILHOS
	}
	

	/**
	 * ORADB: MBCT_GPC_BRI, MBCT_GPC_BRU
	 * 
	 * @param mbcGrupoProcedCirurgico
	 */
	public void persistirMbcGrupoProcedCirurgico(MbcGrupoProcedCirurgico mbcGrupoProcedCirurgico) {
		if (mbcGrupoProcedCirurgico.getSeq() == null) {
			if (mbcGrupoProcedCirurgico.getSituacao() == null) {
				mbcGrupoProcedCirurgico.setSituacao(DominioSituacao.A);
			}
			
			mbcGrupoProcedCirurgico.setCriadoEm(new Date());
			this.getMbcGrupoProcedCirurgicoDAO().persistir(mbcGrupoProcedCirurgico);
		} else {
			MbcGrupoProcedCirurgico mbcGrupo = this.getMbcGrupoProcedCirurgicoDAO().obterPorChavePrimaria(mbcGrupoProcedCirurgico.getSeq());
			if (mbcGrupo != null) {
				mbcGrupo.setCriadoEm(mbcGrupoProcedCirurgico.getCriadoEm());
				mbcGrupo.setDescricao(mbcGrupoProcedCirurgico.getDescricao());
				mbcGrupo.setRapServidores(mbcGrupoProcedCirurgico.getRapServidores());
				mbcGrupo.setSituacao(mbcGrupoProcedCirurgico.getSituacao());
				if (mbcGrupoProcedCirurgico.getMbcProcedimentoPorGrupoes() != null) {
					mbcGrupo.setMbcProcedimentoPorGrupoes(mbcGrupoProcedCirurgico.getMbcProcedimentoPorGrupoes());
				}
				this.getMbcGrupoProcedCirurgicoDAO().persistir(mbcGrupo);
			}
		}
		this.getMbcGrupoProcedCirurgicoDAO().flush();
	}
	
	public void removerMbcGrupoProcedCirurgico(MbcGrupoProcedCirurgico mbcGrupoProcedCirurgico) throws ApplicationBusinessException {
		final List<MbcProcedimentoPorGrupo> lista = getMbcProcedimentoPorGrupoDAO().listarMbcProcedimentoPorGrupoPorMbcGrupoProcedCirurgico(mbcGrupoProcedCirurgico);
		if(lista != null && !lista.isEmpty()){
			throw new ApplicationBusinessException(MbcGrupoProcedCirurgicoRNExceptionCode.MBC_GRUPO_PROCED_CIRURGICO_POSSUI_FILHOS, mbcGrupoProcedCirurgico.getDescricao());	
		}

		MbcGrupoProcedCirurgico mbcGrupo = this.getMbcGrupoProcedCirurgicoDAO().obterPorChavePrimaria(mbcGrupoProcedCirurgico.getSeq());
		if (mbcGrupo != null) {
			this.getMbcGrupoProcedCirurgicoDAO().remover(mbcGrupo);
			this.getMbcGrupoProcedCirurgicoDAO().flush();
		}
	}
	
	protected MbcProcedimentoPorGrupoDAO getMbcProcedimentoPorGrupoDAO() {
		return mbcProcedimentoPorGrupoDAO;
	}
	
	protected MbcGrupoProcedCirurgicoDAO getMbcGrupoProcedCirurgicoDAO(){
		return mbcGrupoProcedCirurgicoDAO;
	}
}