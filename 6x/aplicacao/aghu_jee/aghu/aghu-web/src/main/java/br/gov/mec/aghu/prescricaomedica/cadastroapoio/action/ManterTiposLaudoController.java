package br.gov.mec.aghu.prescricaomedica.cadastroapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MpmTextoPadraoLaudo;
import br.gov.mec.aghu.model.MpmTipoLaudo;
import br.gov.mec.aghu.model.MpmTipoLaudoConvenio;
import br.gov.mec.aghu.model.MpmTipoLaudoConvenioId;
import br.gov.mec.aghu.model.MpmTipoLaudoTextoPadrao;
import br.gov.mec.aghu.model.MpmTipoLaudoTextoPadraoId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterTiposLaudoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1388450404492976163L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;

	private MpmTipoLaudo tipoLaudo;

	private Short seq;

	private boolean edicao;

	private List<MpmTipoLaudoConvenio> convenios;

	private List<MpmTipoLaudoTextoPadrao> textosPadrao;

	private MpmTextoPadraoLaudo textoPadrao;

	private FatConvenioSaudePlano convenioSaudePlano;

	private Short convenioId;

	private Byte planoId;

	private MpmTipoLaudoConvenioId tipoLaudoConvenioId;

	private MpmTipoLaudoTextoPadraoId tipoLaudoTextoPadraoId;

	private final String PAGE_MANTER_TEXTO_PADRAO_LIST = "manterTextoPadraoLaudoList";
	private final String PAGE_TIPO_LAUDO_LIST = "manterTiposLaudoList";
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	public String redirecionarTextoPadrao() {
		return PAGE_MANTER_TEXTO_PADRAO_LIST;
	}
	
	public void inicio() {
	 

		this.tipoLaudoConvenioId = null;
		this.tipoLaudoTextoPadraoId = null;
		this.textoPadrao = null;
		this.convenioSaudePlano = null;
		
		if (this.seq != null && this.edicao) {
			this.tipoLaudo = this.prescricaoMedicaFacade.obterMpmTipoLaudoPorChavePrimaria(this.seq);
		}

		if (this.tipoLaudo == null) {
			this.tipoLaudo = new MpmTipoLaudo();
			tipoLaudo.setSituacao(DominioSituacao.A);
			tipoLaudo.setInformaTempoTratamento(Boolean.TRUE);
			tipoLaudo.setLaudoUnicoAtend(Boolean.TRUE);
			
			this.convenios = new ArrayList<MpmTipoLaudoConvenio>(0);
			this.textosPadrao = new ArrayList<MpmTipoLaudoTextoPadrao>(0);			

			this.edicao = false;
		} else {
			this.edicao = true;

			if (this.tipoLaudo.getTiposLaudoConvenio() != null) {
				this.convenios = new ArrayList<MpmTipoLaudoConvenio>(this.prescricaoMedicaFacade.pesquisarTipoLaudoConvenio(this.tipoLaudo.getSeq()));
			} else {
				this.convenios = new ArrayList<MpmTipoLaudoConvenio>(0);
			}

			if (this.tipoLaudo.getTiposLaudoTextoPadrao() != null) {
				this.textosPadrao = new ArrayList<MpmTipoLaudoTextoPadrao>(this.prescricaoMedicaFacade.pesquisarTipoLaudoTextoPadrao(this.tipoLaudo.getSeq()));
			} else {
				this.textosPadrao = new ArrayList<MpmTipoLaudoTextoPadrao>(0);
			}
		}
	
	}

	public void salvarTipoLaudo() {
		try {
			if(this.tipoLaudo != null && !tipoLaudo.getLaudoUnicoAtend() && tipoLaudo.getTempoValidade() == null){
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_TIPO_LAUDO_TEMPO_VALIDADE");
			} else {
				if (this.tipoLaudo.getSeq() == null) {
					this.prescricaoMedicaFacade.inserirMpmTipoLaudo(this.tipoLaudo, true);
					
					this.seq = this.tipoLaudo.getSeq();
					
					this.inicio();
					
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_TIPO_LAUDO");
				} else {
					this.prescricaoMedicaFacade.atualizarMpmTipoLaudo(this.tipoLaudo, true);
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_TIPO_LAUDO");
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void salvarConvenio() {
		try {
			MpmTipoLaudoConvenio auxTipoLaudoConvenio = new MpmTipoLaudoConvenio();
			auxTipoLaudoConvenio.setId(new MpmTipoLaudoConvenioId(this.convenioSaudePlano.getId().getSeq(), this.convenioSaudePlano
					.getId().getCnvCodigo(), this.tipoLaudo.getSeq()));

			this.prescricaoMedicaFacade.inserirMpmTipoLaudoConvenio(auxTipoLaudoConvenio, true);

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_CONVENIO");

			// Limpa o fieldset de convênio
			this.atribuirPlano(null);

			// Recarrega as informações da tela
			this.recarregarInformacoesTela();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void excluirConvenio() {
		if (this.tipoLaudoConvenioId != null) {
			MpmTipoLaudoConvenio tipoLaudoConvenio = this.prescricaoMedicaFacade
					.obterMpmTipoLaudoConvenioPorChavePrimaria(this.tipoLaudoConvenioId);
			
			if (tipoLaudoConvenio != null) {
				this.prescricaoMedicaFacade.removerMpmTipoLaudoConvenio(tipoLaudoConvenio, true);
			}

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_CONVENIO");

			// Recarrega as informações da tela
			this.recarregarInformacoesTela();
		} else {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_EXCLUSAO_CONVENIO");
		}
	}

	public void salvarTextoPadrao() {
		try {
			MpmTipoLaudoTextoPadrao auxTipoLaudoTextoPadrao = new MpmTipoLaudoTextoPadrao();
			auxTipoLaudoTextoPadrao.setId(new MpmTipoLaudoTextoPadraoId(this.tipoLaudo.getSeq(), this.textoPadrao.getSeq()));

			this.prescricaoMedicaFacade.inserirMpmTipoLaudoTextoPadrao(auxTipoLaudoTextoPadrao, true);

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_TEXTO_PADRAO");

			// Limpa o fieldset de texto padrão
			this.textoPadrao = null;

			// Recarrega as informações da tela
			this.recarregarInformacoesTela();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_CRIACAO_TEXTO_PADRAO");
		}
	}

	public void excluirTextoPadrao() {
		if (this.tipoLaudoTextoPadraoId != null) {
			MpmTipoLaudoTextoPadrao tipoLaudoTextoPadrao = this.prescricaoMedicaFacade
					.obterMpmTipoLaudoTextoPadraoPorChavePrimaria(this.tipoLaudoTextoPadraoId);
			
			if (tipoLaudoTextoPadrao != null) {
				this.prescricaoMedicaFacade.removerMpmTipoLaudoTextoPadrao(tipoLaudoTextoPadrao, true);
			}

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_TEXTO_PADRAO");

			// Recarrega as informações da tela
			this.recarregarInformacoesTela();
		} else {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_EXCLUSAO_TEXTO_PADRAO");
		}
	}

	public void recarregarInformacoesTela() {
		this.inicio();
	}

	public Long pesquisarTextosPadraoCount(String filtro) {
		return this.prescricaoMedicaFacade.pesquisarTextosPadraoCount((String) filtro);
	}

	public List<MpmTextoPadraoLaudo> pesquisarTextosPadrao(String filtro) {
		return this.returnSGWithCount(this.prescricaoMedicaFacade.pesquisarTextosPadrao((String) filtro),pesquisarTextosPadraoCount(filtro));
	}

	public Long pesquisarConvenioSaudePlanosCount(String filtro) {
		return this.faturamentoApoioFacade.pesquisarConvenioSaudePlanosCount((String) filtro);
	}

	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(String filtro) {
		return this.returnSGWithCount(this.faturamentoApoioFacade.pesquisarConvenioSaudePlanos((String) filtro),pesquisarConvenioSaudePlanosCount(filtro));
	}

	public void escolherPlanoConvenio() {
		if (this.planoId != null && this.convenioId != null) {
			this.convenioSaudePlano = this.faturamentoApoioFacade.obterPlanoPorId(this.planoId, this.convenioId);
			if (this.convenioSaudePlano == null) {
				apresentarMsgNegocio(Severity.WARN, "MENSAGEM_CONVENIO_PLANO_NAO_ENCONTRADO",
						this.convenioId, this.planoId);
			}
		}
	}

	public void atribuirPlano(FatConvenioSaudePlano plano) {
		if (plano != null) {
			this.convenioSaudePlano = plano;
			this.convenioId = plano.getConvenioSaude().getCodigo();
			this.planoId = plano.getId().getSeq();
		} else {
			this.convenioSaudePlano = null;
			this.convenioId = null;
			this.planoId = null;
		}
	}

	public void atribuirPlanoSug() {
		if (this.convenioSaudePlano != null) {
			this.convenioId = this.convenioSaudePlano.getConvenioSaude().getCodigo();
			this.planoId = this.convenioSaudePlano.getId().getSeq();
		} else {
			this.convenioSaudePlano = null;
			this.convenioId = null;
			this.planoId = null;
		}
	}

	public String cancelar() {
		this.seq = null;
		this.tipoLaudo = null;
		return PAGE_TIPO_LAUDO_LIST;
	}

	public MpmTipoLaudo getTipoLaudo() {
		return tipoLaudo;
	}

	public void setTipoLaudo(MpmTipoLaudo tipoLaudo) {
		this.tipoLaudo = tipoLaudo;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public boolean isEdicao() {
		return edicao;
	}

	public void setEdicao(boolean edicao) {
		this.edicao = edicao;
	}

	public List<MpmTipoLaudoConvenio> getConvenios() {
		return convenios;
	}

	public void setConvenios(List<MpmTipoLaudoConvenio> convenios) {
		this.convenios = convenios;
	}

	public List<MpmTipoLaudoTextoPadrao> getTextosPadrao() {
		return textosPadrao;
	}

	public void setTextosPadrao(List<MpmTipoLaudoTextoPadrao> textosPadrao) {
		this.textosPadrao = textosPadrao;
	}

	public MpmTextoPadraoLaudo getTextoPadrao() {
		return textoPadrao;
	}

	public void setTextoPadrao(MpmTextoPadraoLaudo textoPadrao) {
		this.textoPadrao = textoPadrao;
	}

	public FatConvenioSaudePlano getConvenioSaudePlano() {
		return convenioSaudePlano;
	}

	public void setConvenioSaudePlano(FatConvenioSaudePlano convenioSaudePlano) {
		this.convenioSaudePlano = convenioSaudePlano;
	}

	public Short getConvenioId() {
		return convenioId;
	}

	public void setConvenioId(Short convenioId) {
		this.convenioId = convenioId;
	}

	public Byte getPlanoId() {
		return planoId;
	}

	public void setPlanoId(Byte planoId) {
		this.planoId = planoId;
	}

	public MpmTipoLaudoConvenioId getTipoLaudoConvenioId() {
		return tipoLaudoConvenioId;
	}

	public void setTipoLaudoConvenioId(MpmTipoLaudoConvenioId tipoLaudoConvenioId) {
		this.tipoLaudoConvenioId = tipoLaudoConvenioId;
	}

	public MpmTipoLaudoTextoPadraoId getTipoLaudoTextoPadraoId() {
		return tipoLaudoTextoPadraoId;
	}

	public void setTipoLaudoTextoPadraoId(MpmTipoLaudoTextoPadraoId tipoLaudoTextoPadraoId) {
		this.tipoLaudoTextoPadraoId = tipoLaudoTextoPadraoId;
	}
}
