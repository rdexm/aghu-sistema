package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmAltaOtrProcedimento;
import br.gov.mec.aghu.model.MpmAltaOtrProcedimentoId;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmPrescricaoNpt;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaOtrProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmProcedEspecialDiversoDAO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaPrescricaoProcedimentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaProcedimentosVO;
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
public class ManterAltaOtrProcedimentoON extends BaseBusiness {


@EJB
private ManterAltaOtrProcedimentoRN manterAltaOtrProcedimentoRN;

private static final Log LOG = LogFactory.getLog(ManterAltaOtrProcedimentoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaSumarioDAO mpmAltaSumarioDAO;

@Inject
private MpmPrescricaoNptDAO mpmPrescricaoNptDAO;

@EJB
private IComprasFacade comprasFacade;

@Inject
private MpmPrescricaoProcedimentoDAO mpmPrescricaoProcedimentoDAO;

@EJB
private IBlocoCirurgicoFacade blocoCirurgicoFacade;

@Inject
private MpmAltaOtrProcedimentoDAO mpmAltaOtrProcedimentoDAO;

@Inject
private MpmProcedEspecialDiversoDAO mpmProcedEspecialDiversoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2083753943426123559L;

	public enum ManterAltaOtrProcedimentoONExceptionCode implements
			BusinessExceptionCode {

		DATA_OBRIGATORIA, PROCEDIMENTO_OBRIGATORIO, DESCRICAO_OBRIGATORIA;

		public void throwException() throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this);
		}

	}
	
	/**
	 * Gera novo MpmAltaOtrProcedimento para alta sumários
	 * @param altaSumario
	 * @throws BaseException
	 */
	public void gerarAltaOtrProcedimento(MpmAltaSumario altaSumario) throws BaseException{
		
		Integer altanAtdSeq = altaSumario.getId().getApaAtdSeq();
		Integer altanApaSeq = altaSumario.getId().getApaSeq();
		
		List<MpmPrescricaoProcedimento> listaProcedimentos = this.getMpmPrescricaoProcedimentoDAO().obterProcedimentosPendentes(altanAtdSeq);
		List<MpmPrescricaoNpt> listaPrescricaoNpt = this.getMpmPrescricaoNptDAO().obterProcedimentosPendentes(altanAtdSeq);

		if ((listaProcedimentos != null) && (listaProcedimentos.size() > 0)) {
						
			for (MpmPrescricaoProcedimento procedimento : listaProcedimentos) {
				
				if ((procedimento.getMatCodigo() != null) && !this.getMpmAltaOtrProcedimentoDAO().possuiMaterialSumarioAlta(altanAtdSeq, altanApaSeq, procedimento.getMatCodigo().getCodigo())) {
					
					MpmAltaOtrProcedimento altaOtrProcedimento = new MpmAltaOtrProcedimento();
					
					Date dataMinima = this.getMpmPrescricaoProcedimentoDAO().obterDataInicioMinimaPorMaterial(altanAtdSeq, procedimento.getMatCodigo().getCodigo());
					// corrrecao mantis #930
					String nomeMaterial = procedimento.getMatCodigo().getNome();
					
					this.popularAltaOtrProcedimento(altaSumario, altaOtrProcedimento, dataMinima, nomeMaterial);
					altaOtrProcedimento.setMatCodigo(procedimento.getMatCodigo());
					this.getMpmAltaOtrProcedimentoDAO().desatachar(altaOtrProcedimento);
					this.getManterAltaOtrProcedimentoRN().inserirAltaOtrProcedimento(altaOtrProcedimento);
				
				} else if ((procedimento.getProcedimentoCirurgico() != null) && !this.getMpmAltaOtrProcedimentoDAO().possuiProcedimentoCirurgico(altanAtdSeq, altanApaSeq, procedimento.getProcedimentoCirurgico().getSeq())) {
					
					MpmAltaOtrProcedimento altaOtrProcedimento = new MpmAltaOtrProcedimento();
					
					Date dataMinima = this.getMpmPrescricaoProcedimentoDAO().obterDataInicioMinimaPorProcedCirurgico(altanAtdSeq, procedimento.getProcedimentoCirurgico().getSeq());
					String nomeProcedimento = procedimento.getProcedimentoCirurgico().getDescricao();
					
					this.popularAltaOtrProcedimento(altaSumario, altaOtrProcedimento, dataMinima, nomeProcedimento);
					altaOtrProcedimento.setMbcProcedimentoCirurgicos(procedimento.getProcedimentoCirurgico());
					this.getMpmAltaOtrProcedimentoDAO().desatachar(altaOtrProcedimento);
					this.getManterAltaOtrProcedimentoRN().inserirAltaOtrProcedimento(altaOtrProcedimento);
					
				} else if ((procedimento.getProcedimentoEspecialDiverso() != null)
						&& !this
								.getMpmAltaOtrProcedimentoDAO()
								.possuiProcedimentoDiverso(
										altanAtdSeq,
										altanApaSeq,
										procedimento
												.getProcedimentoEspecialDiverso()
												.getSeq())
						&& procedimento.getProcedimentoEspecialDiverso()
								.getImpressoSumarioAlta()) {
					
					MpmAltaOtrProcedimento altaOtrProcedimento = new MpmAltaOtrProcedimento();
					
					Date dataMinima = this.getMpmPrescricaoProcedimentoDAO().obterDataInicioMinimaPorProcedDiverso(altanAtdSeq, procedimento.getProcedimentoEspecialDiverso().getSeq());
					String nomeProcedimento = procedimento.getProcedimentoEspecialDiverso().getDescricao();
					
					this.popularAltaOtrProcedimento(altaSumario, altaOtrProcedimento, dataMinima, nomeProcedimento);
					altaOtrProcedimento.setMpmProcedEspecialDiversos(procedimento.getProcedimentoEspecialDiverso());
					this.getMpmAltaOtrProcedimentoDAO().desatachar(altaOtrProcedimento);
					this.getManterAltaOtrProcedimentoRN().inserirAltaOtrProcedimento(altaOtrProcedimento);
					
				}
								
			}
			
		}
		
		if ((listaPrescricaoNpt != null) && (listaPrescricaoNpt.size() > 0)) {
			
			for (MpmPrescricaoNpt prescricaoNpt : listaPrescricaoNpt) {
				
				if (!this.getMpmAltaOtrProcedimentoDAO().possuiProcedimentoDiverso(altanAtdSeq, altanApaSeq, prescricaoNpt.getProcedEspecialDiversos().getSeq())) {
					
					MpmAltaOtrProcedimento altaOtrProcedimento = new MpmAltaOtrProcedimento();
					
					Date dataMinima = this.getMpmPrescricaoNptDAO().obterDataInicioMinimaPrescricaoNpt(altanAtdSeq, prescricaoNpt.getProcedEspecialDiversos().getSeq());
					String nomeProcedimento = prescricaoNpt.getProcedEspecialDiversos().getDescricao();
					
					this.popularAltaOtrProcedimento(altaSumario, altaOtrProcedimento, dataMinima, nomeProcedimento);
					altaOtrProcedimento.setMpmProcedEspecialDiversos(prescricaoNpt.getProcedEspecialDiversos());
					this.getMpmAltaOtrProcedimentoDAO().desatachar(altaOtrProcedimento);
					this.getManterAltaOtrProcedimentoRN().inserirAltaOtrProcedimento(altaOtrProcedimento);
				
				}
								
			}
			
		}
		
	}
	
	/**
	 * Popula MpmAltaOtrProcedimento
	 * @param altaSumario
	 * @param altaOtrProcedimento
	 * @param dataInicio
	 * @param descricao
	 */
	private void popularAltaOtrProcedimento(MpmAltaSumario altaSumario, MpmAltaOtrProcedimento altaOtrProcedimento, Date dataInicio, String descricao) {
		altaOtrProcedimento.setIndCarga(true);
		altaOtrProcedimento.setIndSituacao(DominioSituacao.A);
		altaOtrProcedimento.setDescOutProcedimento(WordUtils.capitalize(descricao));
		altaOtrProcedimento.setDthrOutProcedimento(dataInicio);
		altaOtrProcedimento.setMpmAltaSumarios(altaSumario);
	}
	
	/**
	 * Atualiza outro procedimento do sumario ativo
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 * @throws ApplicationBusinessException
	 */
	public void versionarAltaOtrProcedimento(MpmAltaSumario altaSumario, Short antigoAsuSeqp) throws ApplicationBusinessException {
		
		List<MpmAltaOtrProcedimento> lista = this.getMpmAltaOtrProcedimentoDAO().obterMpmAltaOtrProcedimento(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), antigoAsuSeqp);
		
		for (MpmAltaOtrProcedimento altaOtrProcedimento : lista) {
			MpmAltaOtrProcedimento novoAltaOtrProcedimento = new MpmAltaOtrProcedimento();
			
			// ----- inicio mantis #930
			// Verificacao para os Items que jah estao gerados.
			// Correcao efetuada em ManterAltaOtrProcedimentoON.gerarAltaOtrProcedimento.
			String descOutProcedimento = altaOtrProcedimento.getDescOutProcedimento();
			if (StringUtils.isBlank(descOutProcedimento)) {
				if (altaOtrProcedimento.getMatCodigo() != null) {
					descOutProcedimento = altaOtrProcedimento.getMatCodigo().getNome();
				}
				if (altaOtrProcedimento.getMbcProcedimentoCirurgicos() != null) {
					descOutProcedimento = altaOtrProcedimento.getMbcProcedimentoCirurgicos().getDescricao();
				}
				if (altaOtrProcedimento.getMpmProcedEspecialDiversos() != null) {
					descOutProcedimento = altaOtrProcedimento.getMpmProcedEspecialDiversos().getDescricao();
				}
			}
			novoAltaOtrProcedimento.setDescOutProcedimento(descOutProcedimento);
			// ----- fim mantis #930
			
			novoAltaOtrProcedimento.setMatCodigo(altaOtrProcedimento.getMatCodigo());
			novoAltaOtrProcedimento.setMbcProcedimentoCirurgicos(altaOtrProcedimento.getMbcProcedimentoCirurgicos());
			novoAltaOtrProcedimento.setMpmProcedEspecialDiversos(altaOtrProcedimento.getMpmProcedEspecialDiversos());
			
			novoAltaOtrProcedimento.setMpmAltaSumarios(altaSumario);
			novoAltaOtrProcedimento.setComplOutProcedimento(altaOtrProcedimento.getComplOutProcedimento());
			novoAltaOtrProcedimento.setDthrOutProcedimento(altaOtrProcedimento.getDthrOutProcedimento());
			novoAltaOtrProcedimento.setIndCarga(altaOtrProcedimento.getIndCarga());
			novoAltaOtrProcedimento.setIndSituacao(altaOtrProcedimento.getIndSituacao());
			
			this.getManterAltaOtrProcedimentoRN().inserirAltaOtrProcedimento(novoAltaOtrProcedimento);
			
		}
		
	}
	
	public void inserirAltaOtrProcedimento(SumarioAltaPrescricaoProcedimentoVO vo, Date dataProcedimento) throws ApplicationBusinessException {

		if (dataProcedimento == null) {
			ManterAltaOtrProcedimentoONExceptionCode.DATA_OBRIGATORIA.throwException();
		}
		if ((vo.getMatCodigo() == null) && (vo.getPciSeq() == null) && (vo.getPedSeq() == null)) {
			ManterAltaOtrProcedimentoONExceptionCode.PROCEDIMENTO_OBRIGATORIO.throwException();
		}
		
		String descricao = vo.getDescricao();
		
		ScoMaterial matCodigo = null;
		MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos = null;
		MpmProcedEspecialDiversos mpmProcedEspecialDiversos = null;
		
		if (vo.getMatCodigo() != null) {
			matCodigo = getComprasFacade().obterScoMaterialPorChavePrimaria(vo.getMatCodigo());
		}
		
		if (vo.getPciSeq() != null) {
			mbcProcedimentoCirurgicos = getBlocoCirurgicoFacade().obterMbcProcedimentoCirurgicosPorId(vo.getPciSeq());
		}
		
		if (vo.getPedSeq() != null) { 
			MpmProcedEspecialDiversoDAO dao = this.getMpmProcedEspecialDiversoDAO();
			mpmProcedEspecialDiversos = dao.obterPorChavePrimaria(vo.getPedSeq());
		}

		MpmAltaSumario altaSumario = this.getMpmAltaSumarioDAO().obterPorChavePrimaria(vo.getId());
		
		MpmAltaOtrProcedimentoId id = new MpmAltaOtrProcedimentoId();
		id.setAsuApaAtdSeq(vo.getId().getApaAtdSeq());
		id.setAsuApaSeq(vo.getId().getApaSeq());
		id.setAsuSeqp(vo.getId().getSeqp());

		MpmAltaOtrProcedimento otrProcedimento = new MpmAltaOtrProcedimento();
		otrProcedimento.setId(id);
		otrProcedimento.setMpmAltaSumarios(altaSumario);
		otrProcedimento.setIndSituacao(DominioSituacao.A);
		otrProcedimento.setIndCarga(vo.getOrigemListaCombo());
		otrProcedimento.setDthrOutProcedimento(dataProcedimento);
		otrProcedimento.setDescOutProcedimento(descricao);
		otrProcedimento.setMatCodigo(matCodigo);
		otrProcedimento.setMbcProcedimentoCirurgicos(mbcProcedimentoCirurgicos);
		otrProcedimento.setMpmProcedEspecialDiversos(mpmProcedEspecialDiversos);

		this.getManterAltaOtrProcedimentoRN().inserirAltaOtrProcedimento(otrProcedimento);
	}

	public void atualizarAltaOtrProcedimento(SumarioAltaPrescricaoProcedimentoVO vo, Date dataProcedimento) throws ApplicationBusinessException{
		if (dataProcedimento == null) {
			ManterAltaOtrProcedimentoONExceptionCode.DATA_OBRIGATORIA.throwException();
		}
		
		MpmAltaOtrProcedimentoId id = new MpmAltaOtrProcedimentoId();
		id.setAsuApaAtdSeq(vo.getId().getApaAtdSeq());
		id.setAsuApaSeq(vo.getId().getApaSeq());
		id.setAsuSeqp(vo.getId().getSeqp());
		id.setSeqp(vo.getSeqp());
		
		MpmAltaOtrProcedimento otrProcedimento = this.getMpmAltaOtrProcedimentoDAO().obterPorChavePrimaria(id);
		otrProcedimento.setDthrOutProcedimento(dataProcedimento);
		
		this.getManterAltaOtrProcedimentoRN().atualizarAltaOtrProcedimento(otrProcedimento);
	}

	public void removerAltaOtrProcedimento(SumarioAltaPrescricaoProcedimentoVO vo) throws ApplicationBusinessException {
		MpmAltaOtrProcedimentoId id = new MpmAltaOtrProcedimentoId();
		id.setAsuApaAtdSeq(vo.getId().getApaAtdSeq());
		id.setAsuApaSeq(vo.getId().getApaSeq());
		id.setAsuSeqp(vo.getId().getSeqp());
		id.setSeqp(vo.getSeqp());
		
		MpmAltaOtrProcedimento otrProcedimento = this.getMpmAltaOtrProcedimentoDAO().obterPorChavePrimaria(id);
		otrProcedimento.setIndSituacao(DominioSituacao.I);
		
		this.getManterAltaOtrProcedimentoRN().atualizarAltaOtrProcedimento(otrProcedimento);
	}
	
	public void inserirAltaOtrProcedimento(SumarioAltaProcedimentosVO vo) throws ApplicationBusinessException{
		Date data = vo.getData();
		String complemento = vo.getDescricao();
		
		if (data == null) {
			ManterAltaOtrProcedimentoONExceptionCode.DATA_OBRIGATORIA.throwException();
		}
		if (StringUtils.isBlank(complemento)) {
			ManterAltaOtrProcedimentoONExceptionCode.DESCRICAO_OBRIGATORIA.throwException();
		}
						
		MpmAltaSumario altaSumario = this.getMpmAltaSumarioDAO().obterPorChavePrimaria(vo.getId());
		
		MpmAltaOtrProcedimentoId id = new MpmAltaOtrProcedimentoId();
		id.setAsuApaAtdSeq(vo.getId().getApaAtdSeq());
		id.setAsuApaSeq(vo.getId().getApaSeq());
		id.setAsuSeqp(vo.getId().getSeqp());
		
		MpmAltaOtrProcedimento otrProcedimento = new MpmAltaOtrProcedimento();
		otrProcedimento.setId(id);
		otrProcedimento.setMpmAltaSumarios(altaSumario);
		otrProcedimento.setIndSituacao(DominioSituacao.A);
		otrProcedimento.setIndCarga(Boolean.FALSE);
		otrProcedimento.setDthrOutProcedimento(data);
		otrProcedimento.setComplOutProcedimento(complemento);
		
		this.getManterAltaOtrProcedimentoRN().inserirAltaOtrProcedimento(otrProcedimento);
	}	

	public void atualizarAltaOtrProcedimento(SumarioAltaProcedimentosVO vo, Date dataProcedimento, String complemento) throws ApplicationBusinessException{
		if (dataProcedimento == null) {
			ManterAltaOtrProcedimentoONExceptionCode.DATA_OBRIGATORIA.throwException();
		}
		if (StringUtils.isBlank(complemento)) {
			ManterAltaOtrProcedimentoONExceptionCode.DESCRICAO_OBRIGATORIA.throwException();
		}
		
		MpmAltaOtrProcedimentoId id = new MpmAltaOtrProcedimentoId();
		id.setAsuApaAtdSeq(vo.getId().getApaAtdSeq());
		id.setAsuApaSeq(vo.getId().getApaSeq());
		id.setAsuSeqp(vo.getId().getSeqp());
		id.setSeqp(vo.getSeqp());
		
		MpmAltaOtrProcedimento otrProcedimento = this.getMpmAltaOtrProcedimentoDAO().obterPorChavePrimaria(id);
		otrProcedimento.setDthrOutProcedimento(dataProcedimento);
		otrProcedimento.setComplOutProcedimento(complemento);
		
		this.getManterAltaOtrProcedimentoRN().atualizarAltaOtrProcedimento(otrProcedimento);
	}
	
	public void removerAltaOtrProcedimento(SumarioAltaProcedimentosVO vo) throws ApplicationBusinessException {
		MpmAltaOtrProcedimentoId id = new MpmAltaOtrProcedimentoId();
		id.setAsuApaAtdSeq(vo.getId().getApaAtdSeq());
		id.setAsuApaSeq(vo.getId().getApaSeq());
		id.setAsuSeqp(vo.getId().getSeqp());
		id.setSeqp(vo.getSeqp());
		
		MpmAltaOtrProcedimento otrProcedimento = this.getMpmAltaOtrProcedimentoDAO().obterPorChavePrimaria(id);
				
		this.getManterAltaOtrProcedimentoRN().removerAltaOtrProcedimento(otrProcedimento);
	}
	
	/**
	 * Remove outro procedimento do sumario.
	 * 
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaOtrProcedimento(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		List<MpmAltaOtrProcedimento> lista = this.getMpmAltaOtrProcedimentoDAO().obterMpmAltaOtrProcedimento(
				altaSumario.getId().getApaAtdSeq(), 
				altaSumario.getId().getApaSeq(), 
				altaSumario.getId().getSeqp(),
				false
		);
		
		for (MpmAltaOtrProcedimento altaOtrProcedimento : lista) {
			this.getManterAltaOtrProcedimentoRN().removerAltaOtrProcedimento(altaOtrProcedimento);
		}
	}
	
	protected MpmPrescricaoProcedimentoDAO getMpmPrescricaoProcedimentoDAO() {
		return mpmPrescricaoProcedimentoDAO;
	}
	
	protected MpmAltaOtrProcedimentoDAO getMpmAltaOtrProcedimentoDAO() {
		return mpmAltaOtrProcedimentoDAO;
	}
	
	protected ManterAltaOtrProcedimentoRN getManterAltaOtrProcedimentoRN() {
		return manterAltaOtrProcedimentoRN;
	}
	
	protected MpmPrescricaoNptDAO getMpmPrescricaoNptDAO() {
		return mpmPrescricaoNptDAO;
	}
	
	private MpmAltaSumarioDAO getMpmAltaSumarioDAO() {
		return mpmAltaSumarioDAO;
	}	

	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}
	
	private MpmProcedEspecialDiversoDAO getMpmProcedEspecialDiversoDAO() {
		return mpmProcedEspecialDiversoDAO;
	}

}