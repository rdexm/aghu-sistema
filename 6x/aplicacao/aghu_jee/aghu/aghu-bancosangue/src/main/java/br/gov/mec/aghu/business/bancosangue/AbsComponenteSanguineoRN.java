package br.gov.mec.aghu.business.bancosangue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.bancosangue.dao.AbsComponenteSanguineoDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsComponenteSanguineoJnDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsComponenteSanguineoJn;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;


@Stateless
public class AbsComponenteSanguineoRN extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(AbsComponenteSanguineoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AbsComponenteSanguineoJnDAO absComponenteSanguineoJnDAO;
	
	@Inject
	private AbsComponenteSanguineoDAO absComponenteSanguineoDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	private static final long serialVersionUID = 1998804334731824428L;

	public enum AbsComponenteSanguineoRNExceptionCode implements BusinessExceptionCode {
		ABS_00801,PERM_AFRESE,VERIF_COD_COMP_SANG_EXISTE;
	}
	
	public void persistir(final AbsComponenteSanguineo absComponenteSanguineo, final Boolean edita) throws ApplicationBusinessException{
		
		if(edita){
			atualizaComponenteSanguineo(absComponenteSanguineo);
		}else{
			insereComponenteSanguineo(absComponenteSanguineo);
		}
		
	}

	private void insereComponenteSanguineo(
			final AbsComponenteSanguineo absComponenteSanguineo) throws ApplicationBusinessException {
		preinsert(absComponenteSanguineo);
		verificaCod(absComponenteSanguineo);
		getAbsComponenteSanguineoDAO().persistir(absComponenteSanguineo);
		posinsert(absComponenteSanguineo);
		
	}
	
	public Boolean existeAbsComponenteSanguineo(String codigo) {
		return getAbsComponenteSanguineoDAO().verificaComponenteSanguineoExistente(codigo);
	}
	
	private void verificaCod(final AbsComponenteSanguineo absComponenteSanguineo) throws ApplicationBusinessException {
		if(getAbsComponenteSanguineoDAO().verificaComponenteSanguineoExistente(absComponenteSanguineo.getCodigo())){
			throw new ApplicationBusinessException(AbsComponenteSanguineoRNExceptionCode.VERIF_COD_COMP_SANG_EXISTE);
		}
	}

	private void atualizaComponenteSanguineo(
			final AbsComponenteSanguineo absComponenteSanguineo) throws ApplicationBusinessException {
		final AbsComponenteSanguineo original = getAbsComponenteSanguineoDAO().obterOriginal(absComponenteSanguineo);
		preatualiza(absComponenteSanguineo, original);
		getAbsComponenteSanguineoDAO().merge(absComponenteSanguineo);
		posatualizar(absComponenteSanguineo,original);
		
	}
	
	
	
	/**
	 * ORADB: ABST_CSA_BRI
	 * @param absComponenteSanguineo
	 * @author felipe.cruz
	 * @throws ApplicationBusinessException 
	 */
	private void preinsert(final AbsComponenteSanguineo absComponenteSanguineo)throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		absComponenteSanguineo.setRapServidores(servidorLogado);
		absComponenteSanguineo.setCriadoEm(new Date());
		if((absComponenteSanguineo.getDensidade()!=null)){
			if(absComponenteSanguineo.getDensidade().compareTo(BigDecimal.ZERO) == 0){
				throw new ApplicationBusinessException(AbsComponenteSanguineoRNExceptionCode.ABS_00801);
			}
		}
		if((absComponenteSanguineo.getMensSolicAferese()!= null) && 
				(absComponenteSanguineo.getIndAferese()== false)){
			throw new ApplicationBusinessException(AbsComponenteSanguineoRNExceptionCode.PERM_AFRESE);
		}
	}
	
	/**
	 * ORADB: ABST_CSA_ASI
	 * @param absComponenteSanguineo
	 * @author felipe.cruz
	 * @throws ApplicationBusinessException 
	 *  
	 */
	private void posinsert(final AbsComponenteSanguineo absComponenteSanguineo) throws ApplicationBusinessException {

		inserirFatProcedHospInternos(null,null,null,absComponenteSanguineo.getCodigo(),null,
				absComponenteSanguineo.getDescricao(),absComponenteSanguineo.getIndSituacao(),null, 
					null,null,null);
	}
	
	
	
	/**
	 * ORADB: ABST_CSA_BRU
	 * @param absComponenteSanguineo
	 * @author felipe.cruz
	 *  
	 */
	private void preatualiza(final AbsComponenteSanguineo absComponenteSanguineo, final AbsComponenteSanguineo original ) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		absComponenteSanguineo.setRapServidores(servidorLogado);
		
		if(original.getIndSituacao() != absComponenteSanguineo.getIndSituacao()){
			updateFatProcedHospInternosSituacao(null,null,null,
					absComponenteSanguineo.getCodigo(),null,absComponenteSanguineo.getIndSituacao(),
					null,null,null,null);
		}
		if(absComponenteSanguineo.getDensidade()!=null){
			if(absComponenteSanguineo.getDensidade().compareTo(BigDecimal.ZERO) == 0){
				throw new ApplicationBusinessException(AbsComponenteSanguineoRNExceptionCode.ABS_00801);
			}
		}
		if((absComponenteSanguineo.getMensSolicAferese()!= null) && 
				(absComponenteSanguineo.getIndAferese()== false)){
			throw new ApplicationBusinessException(AbsComponenteSanguineoRNExceptionCode.PERM_AFRESE);
		}
		List<FatProcedHospInternos> listaProcedimentosInternos = this.faturamentoFacade.listarPhi(null, null, null, null, null, absComponenteSanguineo.getCodigo(), null, null, null, 1);
		if (listaProcedimentosInternos == null || listaProcedimentosInternos.isEmpty()) {
			posinsert(absComponenteSanguineo);
		}
	}
	
	/**
	 * ORADB: ABST_CSA_ASU
	 * @param absComponenteSanguineo
	 * @author felipe.cruz
	 * @param original 
	 * @throws ApplicationBusinessException 
	 *  
	 */
	private void posatualizar(final AbsComponenteSanguineo absComponenteSanguineo, final AbsComponenteSanguineo original) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		//inserirFatProcedHospInternos(null,null,null,absComponenteSanguineo.getCodigo(),null,
		//		absComponenteSanguineo.getDescricao(),absComponenteSanguineo.getIndSituacao(),null, 
		//			null,null,null);
		AbsComponenteSanguineoJn jn = new AbsComponenteSanguineoJn();
		if(!absComponenteSanguineo.equals(original)){
			jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AbsComponenteSanguineoJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
			//jn.setDataAlteracao(new Date());
			jn.setCodigo(absComponenteSanguineo.getCodigo());
			jn.setDescricao(absComponenteSanguineo.getDescricao());
			jn.setIndSituacao(absComponenteSanguineo.getIndSituacao());
			jn.setNroDiasValidade(absComponenteSanguineo.getNroDiasValidade());
			jn.setCodTransfusao(absComponenteSanguineo.getCodTransfusao());
			jn.setCodCedenciaBanco(absComponenteSanguineo.getCodCedenciaBanco());
			jn.setCodCedenciaIndustria(absComponenteSanguineo.getCodCedenciaIndustria());
			jn.setCodAutotransfusao(absComponenteSanguineo.getCodAutotransfusao());
			jn.setServidor(absComponenteSanguineo.getRapServidores());
			jn.setIndIrradiado(absComponenteSanguineo.getIndIrradiado());
			jn.setIndAmostra(absComponenteSanguineo.getIndAmostra());
			jn.setIndFiltrado(absComponenteSanguineo.getIndFiltrado());
			jn.setIndLavado(absComponenteSanguineo.getIndFiltrado());
			jn.setCriadoEm(absComponenteSanguineo.getCriadoEm());
			jn.setIndPermitePrescricao(absComponenteSanguineo.getIndPermitePrescricao());
			jn.setIndJustificativa(absComponenteSanguineo.getIndJustificativa());
			jn.setArmazenarEm(absComponenteSanguineo.getArmazenarEm());
			jn.setIndPermitePool(absComponenteSanguineo.getIndPermitePool());
			jn.setIndEtiquetaDoacao(absComponenteSanguineo.getIndEtiquetaDoacao());
			jn.setDescricaoEtiqueta(absComponenteSanguineo.getDescricaoEtiqueta());
			jn.setDensidade(absComponenteSanguineo.getDensidade());
			jn.setIndPermiteRefracionamento(absComponenteSanguineo.getIndPermiteRefracionamento());
			jn.setIndAferese(absComponenteSanguineo.getIndAferese());
			jn.setMensSolicAferese(absComponenteSanguineo.getMensSolicAferese());
			
			getAbsComponenteSanguineoJnDAO().persistir(jn);
		}
	}
	
	
	
	//rn_phip_atu_insert
	public void inserirFatProcedHospInternos(final ScoMaterial matCodigo,
			final MbcProcedimentoCirurgicos pciSeq, final MpmProcedEspecialDiversos pedSeq,
			final String csaCodigo, final String pheCodigo, final String descricao,
			final DominioSituacao indSituacao, final Short euuSeq, final Integer cduSeq,
			final Short cuiSeq, final Integer tidSeq)  throws ApplicationBusinessException {

		final FatProcedHospInternos fat = new FatProcedHospInternos();
		fat.setDescricao(descricao);
		fat.setSituacao(indSituacao);
		fat.setMaterial(matCodigo);
		fat.setProcedimentoCirurgico(pciSeq);
		fat.setProcedimentoEspecialDiverso(pedSeq);
		fat.setCsaCodigo(csaCodigo);
		fat.setPheCodigo(pheCodigo);

		int seq = 0;
		if (euuSeq != null) {
			seq = euuSeq;
		} else {
			if (cduSeq != null) {
				seq = cduSeq;
			} else {
				if (cuiSeq != null) {
					seq = cuiSeq;
				} else {
					if (tidSeq != null) {
						seq = tidSeq;
					} else {
						seq = 0;
					}
				}
			}
		}

		if (seq == 0) {
			fat.setIndOrigemPresc(true);
		} else {
			fat.setIndOrigemPresc(false);
		}

		fat.setEuuSeq(euuSeq);
		fat.setCduSeq(cduSeq);
		fat.setCuiSeq(cuiSeq);
		fat.setTidSeq(tidSeq);
		
		getFaturamentoFacade().inserirFatProcedHospInternos(fat);
		
	}
	
	// fatk_phi_rn.RN_PHIP_ATU_SITUACAO
	private void updateFatProcedHospInternosSituacao(final ScoMaterial matCodigo,
			final MbcProcedimentoCirurgicos pciSeq, final MpmProcedEspecialDiversos pedSeq,
			final String csaCodigo, final String pheCodigo, final DominioSituacao indSituacao, final Short euuSeq, final Integer cduSeq,
			final Short cuiSeq, final Integer tidSeq) throws ApplicationBusinessException {
		

		final FatProcedHospInternos fatProcedHospInternos = buscaFatProcedHospInternosGenerico(matCodigo, pciSeq, pedSeq,
				csaCodigo, pheCodigo, euuSeq, cduSeq, cuiSeq, tidSeq);
		
		getFaturamentoFacade().atualizarFatProcedHospInternos(fatProcedHospInternos);

	}
	
	private FatProcedHospInternos buscaFatProcedHospInternosGenerico(final ScoMaterial matCodigo,
			final MbcProcedimentoCirurgicos pciSeq, final MpmProcedEspecialDiversos pedSeq,
			final String csaCodigo, final String pheCodigo, final Short euuSeq, final Integer cduSeq,
			final Short cuiSeq, final Integer tidSeq) {
		FatProcedHospInternos fatProcedHospInternos = null;
		if (tidSeq != null) {
			fatProcedHospInternos = getFaturamentoFacade().pesquisarPorChaveGenericaFatProcedHospInternos(tidSeq, FatProcedHospInternos.Fields.TDI_SEQ);						
		} else if(matCodigo !=null){
			fatProcedHospInternos = getFaturamentoFacade().pesquisarPorChaveGenericaFatProcedHospInternos(matCodigo.getCodigo(), FatProcedHospInternos.Fields.MAT_CODIGO);
		} else if(pciSeq !=null){
			fatProcedHospInternos = getFaturamentoFacade().pesquisarPorChaveGenericaFatProcedHospInternos(pciSeq.getSeq(), FatProcedHospInternos.Fields.PCI_SEQ);
		} else if(pedSeq !=null){
			fatProcedHospInternos = getFaturamentoFacade().pesquisarPorChaveGenericaFatProcedHospInternos(pedSeq.getSeq(), FatProcedHospInternos.Fields.PROCEDIMENTO_ESPECIAL_DIVERSO_SEQ);
		} else if(csaCodigo !=null){
			fatProcedHospInternos = getFaturamentoFacade().pesquisarPorChaveGenericaFatProcedHospInternos(csaCodigo, FatProcedHospInternos.Fields.CSA_CODIGO);
		} else if(pheCodigo !=null){
			fatProcedHospInternos = getFaturamentoFacade().pesquisarPorChaveGenericaFatProcedHospInternos(pheCodigo, FatProcedHospInternos.Fields.PHE_CODIGO);
		} else if(euuSeq !=null){
			fatProcedHospInternos = getFaturamentoFacade().pesquisarPorChaveGenericaFatProcedHospInternos(euuSeq, FatProcedHospInternos.Fields.EUU_SEQ);
		} else if(cduSeq !=null){
			fatProcedHospInternos = getFaturamentoFacade().pesquisarPorChaveGenericaFatProcedHospInternos(cduSeq, FatProcedHospInternos.Fields.CDU_SEQ);
		} else if(cuiSeq !=null){
			fatProcedHospInternos = getFaturamentoFacade().pesquisarPorChaveGenericaFatProcedHospInternos(cuiSeq, FatProcedHospInternos.Fields.CUI_SEQ);
		}
		return fatProcedHospInternos;
	}
	
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected AbsComponenteSanguineoDAO getAbsComponenteSanguineoDAO(){
		return absComponenteSanguineoDAO;
	}
	
	protected AbsComponenteSanguineoJnDAO getAbsComponenteSanguineoJnDAO(){
		return absComponenteSanguineoJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
