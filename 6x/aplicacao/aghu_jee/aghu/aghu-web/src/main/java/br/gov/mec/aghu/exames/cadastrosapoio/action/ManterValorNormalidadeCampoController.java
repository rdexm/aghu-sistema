package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSexoValorNormalidadeCampo;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioUnidadeMedidaIdade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelUnidMedValorNormal;
import br.gov.mec.aghu.model.AelValorNormalidCampo;
import br.gov.mec.aghu.model.AelValorNormalidCampoId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterValorNormalidadeCampoController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ManterValorNormalidadeCampoController.class);

	private static final long serialVersionUID = -6153762937738535963L;

	@EJB
	private IExamesFacade examesFacade;

	private AelCampoLaudo campoLaudo;
	private AelValorNormalidCampo valorNormalidCampo;
	private AelValorNormalidCampoId normalidadeCampoId;

	/* ID AelCampoLaudo */
	private Integer seq;

	/* ID valorNomaLidCampo */
	private Integer calSeq;
	private Short seqp;

	// Lista dos exames (Grupo tecnica de unidade funcional de exames)
	private List<AelValorNormalidCampo> listaNormalidadesCampoLaudo;
	private String voltarPara; // O padrão é voltar para interface de pesquisa
	private DominioSexoValorNormalidadeCampo sexoValorNormalidade;

	/**
	 * Chamado no inicio de "cada conversacao"
	 */
	public void iniciar() {
	 


		if (this.seq != null) {
			this.normalidadeCampoId = null;
			this.campoLaudo = this.examesFacade.obterAelCampoLaudoId(this.seq);
			this.valorNormalidCampo = new AelValorNormalidCampo();
			valorNormalidCampo.setSituacao(DominioSituacao.A);
			valorNormalidCampo.setUnidMedidaIdade(DominioUnidadeMedidaIdade.A);
			valorNormalidCampo.setUnidMedidaIdadeMin(DominioUnidadeMedidaIdade.A);
			this.pesquisarNormalidadesCampo();

		}

	
	}

	private void pesquisarNormalidadesCampo() {

		if (campoLaudo != null) {
			this.listaNormalidadesCampoLaudo = this.examesFacade.pesquisarNormalidadesCampoLaudo(campoLaudo.getSeq());
		}

	}

	public void alterar() {

		try {
			if (this.calSeq != null && this.seqp != null) {

				// atualiza valorNormalidCampo
				if (valorNormalidCampo.getSituacao() == null) {
					valorNormalidCampo.setSituacao(DominioSituacao.A);
				}
				
				this.examesFacade.atualizarValoresNormalidadeCampo(valorNormalidCampo);
				this.pesquisarNormalidadesCampo();
				this.cancelarEdicao();
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_VALORES_NORMALIDADE");

			}

		} catch (BaseException e) {
			this.cancelarEdicao();
			apresentarExcecaoNegocio(e);
		}
	}

	public String obterRichMinAceitavel(AelValorNormalidCampo valorNormalide) {

		StringBuffer retorno = new StringBuffer();
		if (valorNormalide.getValorMinimoAceitavel() != null) {
			retorno.append("Valor Mínimo Aceitável: ").append(valorNormalide.getValorMinimoAceitavel());
		}

		return retorno.toString();
	}

	public String obterRichMaxAceitavel(AelValorNormalidCampo valorNormalide) {

		StringBuffer retorno = new StringBuffer();
		if (valorNormalide.getValorMaximoAceitavel() != null) {
			retorno.append("Valor Máximo Aceitável: ").append(valorNormalide.getValorMaximoAceitavel());
		}

		return retorno.toString();
	}

	public String obterRichMinAbsurdo(AelValorNormalidCampo valorNormalide) {

		StringBuffer retorno = new StringBuffer();
		if (valorNormalide.getValorMinimoAbsurdo() != null) {
			retorno.append("Valor Mínimo Absurdo: ").append(valorNormalide.getValorMinimoAbsurdo());
		}

		return retorno.toString();
	}

	public String obterRichMaxAbsurdo(AelValorNormalidCampo valorNormalide) {

		StringBuffer retorno = new StringBuffer();

		if (valorNormalide.getValorMaximoAbsurdo() != null) {
			retorno.append("Valor Máximo Absurdo: ").append(valorNormalide.getValorMaximoAbsurdo());
		}

		return retorno.toString();
	}

	public void adicionar() {

		boolean limparIdOnError = (this.valorNormalidCampo != null && this.valorNormalidCampo.getId() == null);

		try {
			// insere valorNormalidCampo
			AelValorNormalidCampoId id = new AelValorNormalidCampoId();
			id.setCalSeq(this.seq);
			this.valorNormalidCampo.setId(id);
			if (valorNormalidCampo.getSituacao() == null) {
				valorNormalidCampo.setSituacao(DominioSituacao.A);
			}
			
			// Converte sexo do valor da normalidade em sexo
			if(this.sexoValorNormalidade != null){
				this.valorNormalidCampo.setSexo(DominioSexo.getInstance(sexoValorNormalidade.toString()));	
			}

			this.examesFacade.inserirValoresNormalidadeCampo(this.valorNormalidCampo);
			this.pesquisarNormalidadesCampo();
			this.cancelarEdicao();
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_VALORES_NORMALIDADE");

		} catch (BaseException e) {
			if (limparIdOnError) {
				valorNormalidCampo.setId(null);
			}
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage());
		}
	}

	public void cancelarEdicao() {
		this.valorNormalidCampo = new AelValorNormalidCampo();
		this.valorNormalidCampo.setSituacao(DominioSituacao.A);
		this.valorNormalidCampo.setUnidMedidaIdade(DominioUnidadeMedidaIdade.A);
		this.valorNormalidCampo.setUnidMedidaIdadeMin(DominioUnidadeMedidaIdade.A);
		this.normalidadeCampoId = null;
		this.calSeq = null;
		this.seqp = null;
		this.sexoValorNormalidade = null;
	}

	public void editarValoresCampo(AelValorNormalidCampo aelValorNormalidCampo) {
		this.valorNormalidCampo = new AelValorNormalidCampo();

		this.valorNormalidCampo.setId(aelValorNormalidCampo.getId());
		this.valorNormalidCampo.setVersion(aelValorNormalidCampo.getVersion());
		this.valorNormalidCampo.setDthrInicial(aelValorNormalidCampo.getDthrInicial());
		this.valorNormalidCampo.setValorMinimo(aelValorNormalidCampo.getValorMinimo());
		this.valorNormalidCampo.setValorMaximo(aelValorNormalidCampo.getValorMaximo());
		this.valorNormalidCampo.setQtdeCasasDecimais(aelValorNormalidCampo.getQtdeCasasDecimais());
		this.valorNormalidCampo.setSituacao(aelValorNormalidCampo.getSituacao());
		this.valorNormalidCampo.setDthrFinal(aelValorNormalidCampo.getDthrFinal());
		this.valorNormalidCampo.setValorMinimoAceitavel(aelValorNormalidCampo.getValorMinimoAceitavel());
		this.valorNormalidCampo.setValorMaximoAceitavel(aelValorNormalidCampo.getValorMaximoAceitavel());
		this.valorNormalidCampo.setValorMinimoAbsurdo(aelValorNormalidCampo.getValorMinimoAbsurdo());
		this.valorNormalidCampo.setValorMaximoAbsurdo(aelValorNormalidCampo.getValorMaximoAbsurdo());
		// Converte sexo em sexo do valor da normalidade 
		final DominioSexo sexo = aelValorNormalidCampo.getSexo(); 
		if(sexo != null){
			this.sexoValorNormalidade = DominioSexoValorNormalidadeCampo.getInstance(sexo.toString());	
		}
		this.valorNormalidCampo.setIdadeMinima(aelValorNormalidCampo.getIdadeMinima());
		this.valorNormalidCampo.setIdadeMaxima(aelValorNormalidCampo.getIdadeMaxima());
		this.valorNormalidCampo.setServidor(aelValorNormalidCampo.getServidor());
		this.valorNormalidCampo.setUnidMedidaIdade(aelValorNormalidCampo.getUnidMedidaIdade());
		this.valorNormalidCampo.setUnidadeMedida(aelValorNormalidCampo.getUnidadeMedida());
		this.valorNormalidCampo.setUnidMedidaIdadeMin(aelValorNormalidCampo.getUnidMedidaIdadeMin());
		this.valorNormalidCampo.setCampoLaudo(aelValorNormalidCampo.getCampoLaudo());

		this.normalidadeCampoId = aelValorNormalidCampo.getId();
		// this.valorNormalidCampo = examesFacade.obterValorNormalidCampoPorChavePrimaria(aelValorNormalidCampoId);
		this.calSeq = aelValorNormalidCampo.getId().getCalSeq();
		this.seqp = aelValorNormalidCampo.getId().getSeqp();

	}

	public List<AelUnidMedValorNormal> pesquisarUnidadesMedida(String parametroConsulta) {
		return this.examesFacade.pesquisarUnidadesValorNormal(parametroConsulta);
	}

	public List<AelUnidMedValorNormal> pesquisarUnidadesMedida() {
		return this.examesFacade.pesquisarUnidadesValorNormal();
	}

	/**
	 * Cancela a insercao ou alteracao na tela
	 */
	public String voltar() {

		String retorno = this.voltarPara;

		this.campoLaudo = null;
		this.valorNormalidCampo = null;
		this.normalidadeCampoId = null;
		this.seq = null;
		this.calSeq = null;
		this.seqp = null;
		this.listaNormalidadesCampoLaudo = null;
		this.voltarPara = null;
		this.sexoValorNormalidade = null;

		return retorno;
	}

	/*
	 * Getters e setters
	 */

	public AelCampoLaudo getCampoLaudo() {
		return campoLaudo;
	}

	public void setCampoLaudo(AelCampoLaudo campoLaudo) {
		this.campoLaudo = campoLaudo;
	}

	public AelValorNormalidCampo getValorNormalidCampo() {
		return valorNormalidCampo;
	}

	public void setValorNormalidCampo(AelValorNormalidCampo valorNormalidCampo) {
		this.valorNormalidCampo = valorNormalidCampo;
	}

	public Integer getCalSeq() {
		return calSeq;
	}

	public void setCalSeq(Integer calSeq) {
		this.calSeq = calSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public List<AelValorNormalidCampo> getListaNormalidadesCampoLaudo() {
		return listaNormalidadesCampoLaudo;
	}

	public void setListaNormalidadesCampoLaudo(List<AelValorNormalidCampo> listaNormalidadesCampoLaudo) {
		this.listaNormalidadesCampoLaudo = listaNormalidadesCampoLaudo;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public AelValorNormalidCampoId getNormalidadeCampoId() {
		return normalidadeCampoId;
	}
	
	public void setNormalidadeCampoId(AelValorNormalidCampoId normalidadeCampoId) {
		this.normalidadeCampoId = normalidadeCampoId;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	
	public DominioSexoValorNormalidadeCampo getSexoValorNormalidade() {
		return sexoValorNormalidade;
	}
	
	public void setSexoValorNormalidade(DominioSexoValorNormalidadeCampo sexoValorNormalidade) {
		this.sexoValorNormalidade = sexoValorNormalidade;
	}

}
