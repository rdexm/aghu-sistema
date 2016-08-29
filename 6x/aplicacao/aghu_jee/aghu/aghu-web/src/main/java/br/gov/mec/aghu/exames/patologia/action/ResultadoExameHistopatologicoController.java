package br.gov.mec.aghu.exames.patologia.action;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;

import br.gov.mec.aghu.dominio.DominioSismamaAdequabilidadeMaterial;
import br.gov.mec.aghu.dominio.DominioSismamaDimensMaxTumor;
import br.gov.mec.aghu.dominio.DominioSismamaExtensaoTumorPele;
import br.gov.mec.aghu.dominio.DominioSismamaGrau;
import br.gov.mec.aghu.dominio.DominioSismamaHistoCadCodigo;
import br.gov.mec.aghu.dominio.DominioSismamaMargensCirurgicas;
import br.gov.mec.aghu.dominio.DominioSismamaProcedimentoCirurgico;
import br.gov.mec.aghu.dominio.DominioSismamaReceptor;
import br.gov.mec.aghu.dominio.DominioSismamaSimNao;
import br.gov.mec.aghu.dominio.DominioSismamaSimNaoNaoSabe;
import br.gov.mec.aghu.exames.sismama.business.ISismamaFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameSismamaVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller para a tela Resultado de Exames Histopatólogico - SISMAMA.
 * 
 * @author dpacheco
 *
 */
public class ResultadoExameHistopatologicoController extends ActionController {

	private static final long serialVersionUID = 6235121051511210175L;

	private static final String LAUDO_UNICO = "exames-laudounico";
	
	private ItemSolicitacaoExameSismamaVO itemSolicitacaoExameSismamaVO;
	
	@EJB
	private ISismamaFacade sismamaFacade;	
	
	private	 String prontuario;
	private	 String nomePaciente;
	private Long numeroAp;
	private Integer lu2Seq;
	private String etapaLaudo;
	private Map<String, Object> respostas = new HashMap<String, Object>();
	private AelItemSolicitacaoExamesId itemSolicitacaoExameId;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		itemSolicitacaoExameId = itemSolicitacaoExameSismamaVO.getItemSolicitacaoExame().getId();
		
		// Popula o hashMap das respostas com os valores default
		respostas.put(DominioSismamaHistoCadCodigo.C_MIC_MICRCALC.name(), DominioSismamaSimNao.NAO);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_OBS.name(), null);
		respostas.put(DominioSismamaHistoCadCodigo.C_RES_ADEQ.name(), DominioSismamaAdequabilidadeMaterial.SATISFATORIO);
		respostas.put(DominioSismamaHistoCadCodigo.C_RES_INS_POR.name(), null);
		respostas.put(DominioSismamaHistoCadCodigo.C_RES_PROCIR.name(), null);
		respostas.put(DominioSismamaHistoCadCodigo.C_TAM_TUMDOM.name(), DominioSismamaDimensMaxTumor.NAO_AVALIAVEL);
		respostas.put(DominioSismamaHistoCadCodigo.C_DIM_TUMSEC.name(), DominioSismamaDimensMaxTumor.NAO_AVALIAVEL);
		respostas.put(DominioSismamaHistoCadCodigo.C_BEN_HIPSATI.name(), Boolean.FALSE);
		respostas.put(DominioSismamaHistoCadCodigo.C_BEN_HIPCATI.name(), Boolean.FALSE);
		respostas.put(DominioSismamaHistoCadCodigo.C_BEN_LOBCATI.name(), Boolean.FALSE);
		respostas.put(DominioSismamaHistoCadCodigo.C_BEN_ADENOS.name(), Boolean.FALSE);
		respostas.put(DominioSismamaHistoCadCodigo.C_BEN_ESDERO.name(), Boolean.FALSE);
		respostas.put(DominioSismamaHistoCadCodigo.C_BEN_FIBROC.name(), Boolean.FALSE);
		respostas.put(DominioSismamaHistoCadCodigo.C_BEN_FIBROA.name(), Boolean.FALSE);
		respostas.put(DominioSismamaHistoCadCodigo.C_BEN_SOLITA.name(), Boolean.FALSE);
		respostas.put(DominioSismamaHistoCadCodigo.C_BEN_MULTI.name(), Boolean.FALSE);
		respostas.put(DominioSismamaHistoCadCodigo.C_BEN_FLORID.name(), Boolean.FALSE);
		respostas.put(DominioSismamaHistoCadCodigo.C_BEN_MASTIT.name(), Boolean.FALSE);
		respostas.put(DominioSismamaHistoCadCodigo.C_BEN_OUTROS.name(), Boolean.FALSE);
		respostas.put(DominioSismamaHistoCadCodigo.C_BEN_OUTDES.name(), null);
		respostas.put(DominioSismamaHistoCadCodigo.C_NEO_MALIG.name(), null);
		respostas.put(DominioSismamaHistoCadCodigo.C_NEO_OUTDES.name(), null);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_MARG.name(), DominioSismamaMargensCirurgicas.NAO_AVALIAVEL);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_ASSSEC.name(), null);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_ASSSESP.name(), null);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_CENT.name(), DominioSismamaSimNaoNaoSabe.NAO_SABE);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_GRAU.name(), DominioSismamaGrau.NAO_AVALIAVEL);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_EMBO.name(), DominioSismamaSimNaoNaoSabe.NAO_SABE);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_PERIN.name(), DominioSismamaSimNaoNaoSabe.NAO_SABE);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_VASC.name(), DominioSismamaSimNaoNaoSabe.NAO_SABE);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_FOCA.name(), DominioSismamaSimNaoNaoSabe.NAO_SABE);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_PELE.name(), DominioSismamaExtensaoTumorPele.NAO_AVALIAVEL);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_GRAD.name(), DominioSismamaSimNaoNaoSabe.NAO_SABE);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_FASC.name(), DominioSismamaSimNaoNaoSabe.NAO_SABE);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_MAM.name(), DominioSismamaSimNaoNaoSabe.NAO_SABE);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_MUSC.name(), DominioSismamaSimNaoNaoSabe.NAO_SABE);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_LINFO.name(), null);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_LINFAV.name(), null);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_LINFCO.name(), null);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_COAL.name(), null);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_EXTR.name(), null);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_RECES.name(), DominioSismamaReceptor.NR);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_RECPR.name(), DominioSismamaReceptor.NR);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_OUTHIS.name(), DominioSismamaSimNao.NAO);
		respostas.put(DominioSismamaHistoCadCodigo.C_HIS_OUTDES.name(), null);
	
		// Alterações SISCAN #33841
		respostas.put(DominioSismamaHistoCadCodigo.C_TEMP_CBIO_IND.name(), Boolean.FALSE);
		respostas.put(DominioSismamaHistoCadCodigo.C_TEMP_CBIO_POR.name(), null);
		respostas.put(DominioSismamaHistoCadCodigo.C_TEMP_CBIO_SUSP.name(), Boolean.FALSE);
		
		// Obtém as respostas para os campos já respondidos anteriormente.
		try {
			sismamaFacade.recuperarRespostasResultadoExameHistopatologico(itemSolicitacaoExameId.getSoeSeq(), itemSolicitacaoExameId.getSeqp(), respostas);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	
	}
	public void atualizaCoreBiopsy(ActionEvent e) {
		Object obj= null;
		try{
			obj = ((javax.faces.component.html.HtmlSelectOneMenu) e.getComponent().getParent()).getValue();
		} catch (Exception ex) {
			obj = respostas.get(DominioSismamaHistoCadCodigo.C_RES_PROCIR.name());
		}
		if ((obj != null && obj instanceof DominioSismamaProcedimentoCirurgico) || obj == null) {
			if (!DominioSismamaProcedimentoCirurgico.BIOPSIA_POR_AGULHA_GROSSA.equals(obj)) {
				respostas.put(DominioSismamaHistoCadCodigo.C_TEMP_CBIO_IND.name(), Boolean.FALSE);
				respostas.put(DominioSismamaHistoCadCodigo.C_TEMP_CBIO_POR.name(), null);
				respostas.put(DominioSismamaHistoCadCodigo.C_TEMP_CBIO_SUSP.name(), Boolean.FALSE);
			} else if (DominioSismamaProcedimentoCirurgico.BIOPSIA_POR_AGULHA_GROSSA.equals(obj)) {
				if (!(Boolean) respostas.get(DominioSismamaHistoCadCodigo.C_TEMP_CBIO_IND.name())) {
					respostas.put(DominioSismamaHistoCadCodigo.C_TEMP_CBIO_POR.name(), null);
				}
			}
		}
	}


	public String gravar() {
		try {
			sismamaFacade.validarRespostasResultadoExameHistopatologico(respostas);
			sismamaFacade.gravarRespostasResultadoExameHistopatologico(itemSolicitacaoExameId.getSoeSeq(), itemSolicitacaoExameId.getSeqp(), respostas);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAR_RESULTADO_EXAME_HISTOPAT_SISMAMA");
			return cancelar();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String cancelar() {
		try {
			sismamaFacade.verificarPreenchimentoExamesSismama(numeroAp,lu2Seq);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return LAUDO_UNICO;
	}
	
	public ItemSolicitacaoExameSismamaVO getItemSolicitacaoExameSismamaVO() {
		return itemSolicitacaoExameSismamaVO;
	}

	public void setItemSolicitacaoExameSismamaVO(
			ItemSolicitacaoExameSismamaVO itemSolicitacaoExameSismamaVO) {
		this.itemSolicitacaoExameSismamaVO = itemSolicitacaoExameSismamaVO;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Map<String, Object> getRespostas() {
		return respostas;
	}

	public void setRespostas(Map<String, Object> respostas) {
		this.respostas = respostas;
	}

	public Long getNumeroAp() {
		return numeroAp;
	}

	public void setNumeroAp(Long numeroAp) {
		this.numeroAp = numeroAp;
	}

	public String getEtapaLaudo() {
		return etapaLaudo;
	}

	public void setEtapaLaudo(String etapaLaudo) {
		this.etapaLaudo = etapaLaudo;
	}
	public Integer getLu2Seq() {
		return lu2Seq;
	}

	public void setLu2Seq(Integer lu2Seq) {
		this.lu2Seq = lu2Seq;
	}

}