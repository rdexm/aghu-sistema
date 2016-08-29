package br.gov.mec.aghu.faturamento.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatConvGrupoItensProcedDAO;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvProcedGrupos;
import br.gov.mec.aghu.model.FatConvProcedGruposId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ConvenioGrupoItemProcedimentoON extends BaseBusiness {


@EJB
private ConvenioGrupoItemProcedimentoRN convenioGrupoItemProcedimentoRN;

private static final Log LOG = LogFactory.getLog(ConvenioGrupoItemProcedimentoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatConvGrupoItensProcedDAO fatConvGrupoItensProcedDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3590298485010175889L;
	public void persistirGrupoItemConvenio(FatConvGrupoItemProced newFat, FatConvGrupoItemProced oldFat, DominioOperacoesJournal operacao) 
	throws ApplicationBusinessException {		
		if(DominioOperacoesJournal.INS.equals(operacao)) {
			getFatConvGrupoItensProcedDAO().obterPorChavePrimaria(newFat.getId());
			getConvenioGrupoItemProcedimentoRN().executarAntesInserirConvenioGrupoItemProcedimento(newFat);
			getFatConvGrupoItensProcedDAO().persistir(newFat);
		}
		else {
			getConvenioGrupoItemProcedimentoRN().executarAntesAtualizarConvenioGrupoItemProcedimento(newFat);
			getConvenioGrupoItemProcedimentoRN().executarAposAtualizarConvenioGrupoItemProcedimento(oldFat, newFat);
            getFatConvGrupoItensProcedDAO().merge(newFat);
		}
	}
	
	public void excluirGrupoItemConvenio(FatConvGrupoItemProced newFat) 
	throws ApplicationBusinessException {
		newFat = getFatConvGrupoItensProcedDAO().obterPorChavePrimaria(newFat.getId());		
		getFatConvGrupoItensProcedDAO().remover(newFat);
		getConvenioGrupoItemProcedimentoRN().executarAposExcluirItemConvenioGrupoItemProcedimento(newFat);
	}

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public FatConvGrupoItemProced clonarGrupoItemConvenio(FatConvGrupoItemProced elemento) 
	throws Exception {
	
		FatConvGrupoItemProced clone = (FatConvGrupoItemProced) BeanUtils.cloneBean(elemento);

		if (elemento.getConvProcedGrupo() != null) {
			FatConvProcedGrupos convProcedGrupo = new FatConvProcedGrupos();
			FatConvProcedGruposId id = new FatConvProcedGruposId(elemento.getConvProcedGrupo().getId().getCphCspSeq(), elemento.getConvProcedGrupo().getId().getCphCspCnvCodigo(), elemento.getConvProcedGrupo().getId().getGrcSeq(), elemento.getConvProcedGrupo().getId().getCphPhoSeq());
			convProcedGrupo.setId(id);
			clone.setConvProcedGrupo(convProcedGrupo);
		}

		if (elemento.getItemProcedHospitalar() != null) {
			FatItensProcedHospitalar itemProcedHosp = new FatItensProcedHospitalar();
			FatItensProcedHospitalarId id = new FatItensProcedHospitalarId();
			id.setPhoSeq(elemento.getItemProcedHospitalar().getId().getPhoSeq());
			id.setSeq(elemento.getItemProcedHospitalar().getId().getSeq());
			itemProcedHosp.setId(id);
			
			clone.setItemProcedHospitalar(itemProcedHosp);
		}

		if (elemento.getProcedimentoHospitalarInterno() != null) {
			FatProcedHospInternos procedHospInternos = new FatProcedHospInternos();
			procedHospInternos.setSeq(elemento.getProcedimentoHospitalarInterno().getSeq());
			clone.setProcedimentoHospitalarInterno(procedHospInternos);
		}
		
		return clone;
	}
	
	protected ConvenioGrupoItemProcedimentoRN getConvenioGrupoItemProcedimentoRN() {
		return convenioGrupoItemProcedimentoRN;
	}

	protected FatConvGrupoItensProcedDAO getFatConvGrupoItensProcedDAO() {
		return fatConvGrupoItensProcedDAO;
	}
	public List<FatConvGrupoItemProced> pesquisarConvenioGrupoItemProcedimento(
			Integer iphSeq, Short iphPhoSeq) throws ApplicationBusinessException {
		return getFatConvGrupoItensProcedDAO().pesquisarConvenioGrupoItemProcedimento(iphSeq, iphPhoSeq);
	}
}
