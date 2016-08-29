package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmAltaCirgRealizada;
import br.gov.mec.aghu.model.MpmAltaCirgRealizadaId;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaCirgRealizadaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaProcedimentosCrgVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterAltaCirgRealizadaON extends BaseBusiness {


@EJB
private ManterAltaCirgRealizadaRN manterAltaCirgRealizadaRN;

private static final Log LOG = LogFactory.getLog(ManterAltaCirgRealizadaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaSumarioDAO mpmAltaSumarioDAO;

@Inject
private MpmAltaCirgRealizadaDAO mpmAltaCirgRealizadaDAO;

@EJB
private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2229598709691662989L;

	public enum ManterAltaCirgRealizadaONExceptionCode implements
			BusinessExceptionCode {

		DATA_OBRIGATORIA, CIRURGIA_OBRIGATORIA;

		public void throwException() throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this);
		}

	}

	/**
	 * Gera novo MPM_ALTA_CIRG_REALIZADAS para alta sumários
	 * @param altaSumario
	 * @param pacCodigo
	 * @throws BaseException
	 */
	public void gerarAltaCirgRealizada(MpmAltaSumario altaSumario, Integer pacCodigo) throws BaseException{
		
		Integer altanAtdSeq = altaSumario.getId().getApaAtdSeq();
		Integer altanApaSeq = altaSumario.getId().getApaSeq();
		List<MbcProcEspPorCirurgias> procCirurgicos = this.getBlocoCirurgicoFacade().obterProcedimentosCirurgicos(altanAtdSeq, pacCodigo);
		
		for (MbcProcEspPorCirurgias procEspPorCirurgias : procCirurgicos) {
			
			if (!this.getMpmAltaCirgRealizadaDAO().possuiCirurgiaRealizada(procEspPorCirurgias, altanAtdSeq, altanApaSeq)) {
				
				MpmAltaCirgRealizada altaCirgRealizada = new MpmAltaCirgRealizada();
				altaCirgRealizada.setAltaSumario(altaSumario);
				altaCirgRealizada.setIndSituacao(DominioSituacao.A);
				altaCirgRealizada.setIndCarga(true);
				altaCirgRealizada.setDthrCirurgia(procEspPorCirurgias.getCirurgia().getData());
				altaCirgRealizada.setDescCirurgia(WordUtils.capitalize(procEspPorCirurgias.getProcedimentoCirurgico().getDescricao()));
				altaCirgRealizada.setProcedimentoEspCirurgia(procEspPorCirurgias);
				altaCirgRealizada.setProcedimentoCirurgico(procEspPorCirurgias.getProcedimentoCirurgico());
				this.getMpmAltaCirgRealizadaDAO().desatachar(altaCirgRealizada);
				this.getManterAltaCirgRealizadaRN().inserirAltaCirgRealizada(altaCirgRealizada);
								
			}
			
		}
		
	}
	
	/**
	 * Atualiza cirurgia realizada do sumario ativo
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 * @throws BaseException
	 */
	public void versionarAltaCirgRealizada(MpmAltaSumario altaSumario, Short antigoAsuSeqp) throws ApplicationBusinessException {
		
		List<MpmAltaCirgRealizada> lista = this.getMpmAltaCirgRealizadaDAO().obterMpmAltaCirgRealizada(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), antigoAsuSeqp);
		
		for (MpmAltaCirgRealizada altaCirgRealizada : lista) {
			
			MpmAltaCirgRealizada novoAltaCirgRealizada = new MpmAltaCirgRealizada();
			novoAltaCirgRealizada.setAltaSumario(altaSumario);
			novoAltaCirgRealizada.setDescCirurgia(altaCirgRealizada.getDescCirurgia());
			novoAltaCirgRealizada.setDthrCirurgia(altaCirgRealizada.getDthrCirurgia());
			novoAltaCirgRealizada.setIndCarga(altaCirgRealizada.getIndCarga());
			novoAltaCirgRealizada.setIndSituacao(altaCirgRealizada.getIndSituacao());
			novoAltaCirgRealizada.setProcedimentoCirurgico(altaCirgRealizada.getProcedimentoCirurgico());
			novoAltaCirgRealizada.setProcedimentoEspCirurgia(altaCirgRealizada.getProcedimentoEspCirurgia());
			this.getManterAltaCirgRealizadaRN().inserirAltaCirgRealizada(novoAltaCirgRealizada);
			
		}
		
	}
	
	public void inserirAltaCirgRealizada(SumarioAltaProcedimentosCrgVO vo, Date dthrCirurgia) throws ApplicationBusinessException{
		Integer seqProcedimentoCirurgico = vo.getSeqProcedimentoCirurgico();
		
		if (dthrCirurgia == null) {
			ManterAltaCirgRealizadaONExceptionCode.DATA_OBRIGATORIA.throwException();
		}
		if (seqProcedimentoCirurgico == null) {
			ManterAltaCirgRealizadaONExceptionCode.CIRURGIA_OBRIGATORIA.throwException();
		}
		
		
		MpmAltaCirgRealizadaId id = new MpmAltaCirgRealizadaId();
		id.setAsuApaAtdSeq(vo.getId().getApaAtdSeq());
		id.setAsuApaSeq(vo.getId().getApaSeq());
		id.setAsuSeqp(vo.getId().getSeqp());
		
		MpmAltaSumario altaSumario = getMpmAltaSumarioDAO().obterPorChavePrimaria(vo.getId());
		
		MpmAltaCirgRealizada altaCirgRealizada = new MpmAltaCirgRealizada();
		altaCirgRealizada.setId(id);
		altaCirgRealizada.setAltaSumario(altaSumario);
		altaCirgRealizada.setIndSituacao(DominioSituacao.A);
		altaCirgRealizada.setIndCarga(vo.getOrigemListaCombo());
		altaCirgRealizada.setDthrCirurgia(dthrCirurgia);
		altaCirgRealizada.setDescCirurgia(vo.getDescricao());
		
		MbcProcedimentoCirurgicos procedimentoCirurgico = null;
		procedimentoCirurgico = getBlocoCirurgicoFacade().obterMbcProcedimentoCirurgicosPorId(seqProcedimentoCirurgico);
		altaCirgRealizada.setProcedimentoCirurgico(procedimentoCirurgico);
				
		if (vo.getProcEspPorCirurgiasId() != null) {
			MbcProcEspPorCirurgias procedimentoEspCirurgia = null;
			procedimentoEspCirurgia = getBlocoCirurgicoFacade().obterMbcProcEspPorCirurgiasPorChavePrimaria(vo.getProcEspPorCirurgiasId());
			altaCirgRealizada.setProcedimentoEspCirurgia(procedimentoEspCirurgia);			
		}
		
		getManterAltaCirgRealizadaRN().inserirAltaCirgRealizada(altaCirgRealizada);
	}
	
	private MpmAltaSumarioDAO getMpmAltaSumarioDAO() {
		return mpmAltaSumarioDAO;
	}

	public void atualizarAltaCirgRealizada(SumarioAltaProcedimentosCrgVO vo, Date dthrCirurgia) throws ApplicationBusinessException {
		if (dthrCirurgia == null) {
			ManterAltaCirgRealizadaONExceptionCode.DATA_OBRIGATORIA.throwException();
		}
		
		MpmAltaCirgRealizadaId id = new MpmAltaCirgRealizadaId();
		id.setAsuApaAtdSeq(vo.getId().getApaAtdSeq());
		id.setAsuApaSeq(vo.getId().getApaSeq());
		id.setAsuSeqp(vo.getId().getSeqp());
		id.setSeqp(vo.getSeqp());
		
		MpmAltaCirgRealizada altaCirgRealizada = getMpmAltaCirgRealizadaDAO().obterPorChavePrimaria(id);
		altaCirgRealizada.setDthrCirurgia(dthrCirurgia);
		
		getManterAltaCirgRealizadaRN().atualizaraltaCirgRealizada(altaCirgRealizada);
	}
	
	public void removerAltaCirgRealizada(SumarioAltaProcedimentosCrgVO vo) throws ApplicationBusinessException {
		MpmAltaCirgRealizadaId id = new MpmAltaCirgRealizadaId();
		id.setAsuApaAtdSeq(vo.getId().getApaAtdSeq());
		id.setAsuApaSeq(vo.getId().getApaSeq());
		id.setAsuSeqp(vo.getId().getSeqp());
		id.setSeqp(vo.getSeqp());
		
		MpmAltaCirgRealizada altaCirgRealizada = getMpmAltaCirgRealizadaDAO().obterPorChavePrimaria(id);
		altaCirgRealizada.setIndSituacao(DominioSituacao.I);
		
		getManterAltaCirgRealizadaRN().atualizaraltaCirgRealizada(altaCirgRealizada);
	}
	
	/**
	 * Remove cirurgia realizada do sumario.
	 * 
	 * @param altaSumario
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaCirgRealizada(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		List<MpmAltaCirgRealizada> lista = this.getMpmAltaCirgRealizadaDAO().obterMpmAltaCirgRealizada(
				altaSumario.getId().getApaAtdSeq(), 
				altaSumario.getId().getApaSeq(), 
				altaSumario.getId().getSeqp(),
				false
		);
		
		for (MpmAltaCirgRealizada altaCirgRealizada : lista) {
			this.getManterAltaCirgRealizadaRN().removerAltaCirgRealizada(altaCirgRealizada);
		}
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}
	
	protected ManterAltaCirgRealizadaRN getManterAltaCirgRealizadaRN() {
		return manterAltaCirgRealizadaRN;
	}
	
	protected MpmAltaCirgRealizadaDAO getMpmAltaCirgRealizadaDAO() {
		return mpmAltaCirgRealizadaDAO;
	}
	
}
