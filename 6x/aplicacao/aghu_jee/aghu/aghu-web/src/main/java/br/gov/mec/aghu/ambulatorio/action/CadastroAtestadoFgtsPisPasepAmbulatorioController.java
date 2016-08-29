package br.gov.mec.aghu.ambulatorio.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndMotivoUsoFgts;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MamAtestados;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroAtestadoFgtsPisPasepAmbulatorioController extends ActionController {

	private static final long serialVersionUID = -5006167350748062545L;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	// Recebido por parâmetro
	
	private AacConsultas consultaSelecionada;
	private List<AghCid> listaCids;
	private List<MamAtestados> mamAtestados;
	private MamAtestados itemSelecionado;
	// Workaround para funcionar o atributo selection da tabela.
	private MamAtestados itemAux;
	private MamAtestados mamAtestado = new MamAtestados();
	private String nomeRadioSelecionado;
	private AghParametros pTipoAtestado;
	private boolean modoEdicao;
	private String textoPersonalizado1;
	private String textoPersonalizado2;
	private String dadosPacientePersonalizado1;
	private String dadosPacientePersonalizado2;
	private static final String TAG_REPLACE_0 = "{0}";
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
		this.modoEdicao = false;
	}
	
	public void inicio(){
		this.recuperaParametro();
		this.limpar();
		this.pesquisar();
	}
	
	public void pesquisar() {
			
		this.mamAtestados = this.ambulatorioFacade.listarAtestadosPorPacienteTipoAtendimento(consultaSelecionada.getNumero(), 
				pTipoAtestado.getVlrNumerico().shortValue());
	}

	public void gravar(){
		if(this.mamAtestado.getAcometidoDe().length() > 500){
			this.apresentarMsgNegocio(Severity.ERROR, "LABEL_ATESTADO_MENSAGEM_VALIDACAO");
		} else {			
				this.mamAtestado.setAcometidoDe(this.mamAtestado.getAcometidoDe().trim());
				if(mamAtestado.getAcometidoDe().equals(StringUtils.EMPTY)){
					this.apresentarMsgNegocio(Severity.ERROR, "LABEL_ATESTADO_MENSAGEM_CAMPO_OBRIGATORIO");
				} else {
					try {
						if(this.mamAtestado.getIndMotivoUsoFgts() == null){
							this.mamAtestado.setIndMotivoUsoFgts(null);
							this.mamAtestado.setEstagioClinicoGeral(this.mamAtestado.getAcometidoDe());
							this.mamAtestado.setAcometidoDe(null);
						} else if(DominioIndMotivoUsoFgts.PH.equals(this.mamAtestado.getIndMotivoUsoFgts())){
							this.mamAtestado.setEstagioClinicoGeral(this.mamAtestado.getAcometidoDe());
							this.mamAtestado.setAcometidoDe(null);
						} else if(DominioIndMotivoUsoFgts.NM.equals(this.mamAtestado.getIndMotivoUsoFgts())){
							this.mamAtestado.setEstagioClinicoGeral(null);
						} else if(DominioIndMotivoUsoFgts.DT.equals(this.mamAtestado.getIndMotivoUsoFgts())){
							this.mamAtestado.setEstagioClinicoGeral(this.mamAtestado.getAcometidoDe());
							this.mamAtestado.setAcometidoDe(null);
						}
						this.ambulatorioFacade.validarValorMinimoNumeroVias(mamAtestado);
						this.ambulatorioFacade.persistirMamAtestadoAmbulatorio(this.getMamAtestado());
						
						if (this.modoEdicao) {
							apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ATESTADO_ALTERACAO_SUCESSO");
						} else {
							apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ATESTADO_INCLUSAO_SUCESSO");
						}
						this.modoEdicao = Boolean.FALSE;
						this.mamAtestado = new MamAtestados();
						this.inicio();
					} catch (ApplicationBusinessException e) {
						apresentarExcecaoNegocio(e);
					}
				}
		}
	}
	
	public void editar(){
		this.modoEdicao = Boolean.TRUE;
		this.mamAtestado = this.ambulatorioFacade.obterAtestadoEAghCidPorSeq(this.itemSelecionado.getSeq());
		definirComposDeTexto(this.mamAtestado.getIndMotivoUsoFgts(), consultaSelecionada.getPaciente().getNome(), consultaSelecionada.getPaciente().getProntuario());
		this.mamAtestado.setAcometidoDe(montarAcometidoDe(this.mamAtestado.getIndMotivoUsoFgts(), this.mamAtestado.getAcometidoDe(), this.mamAtestado.getEstagioClinicoGeral()));
	}
	
	public void excluir(){
		try {
			this.ambulatorioFacade.excluirAtestadoFgtsPis(itemSelecionado);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ATESTADO_EXCLUSAO_SUCESSO");
			this.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void cancelarEdicao() {
		this.modoEdicao = Boolean.FALSE;
		this.mamAtestado = null;
		this.limpar();
	}	
	
	public void limpar() {		
		this.itemSelecionado = null;
		
		// Resetando valores
		this.mamAtestado = new MamAtestados();
		this.mamAtestado.setAipPacientes(this.consultaSelecionada.getPaciente());
		this.mamAtestado.setMamTipoAtestado(this.ambulatorioFacade.obterTipoAtestadoPorSeq(pTipoAtestado.getVlrNumerico().shortValue()));
		this.mamAtestado.setIndPendente(DominioIndPendenteAmbulatorio.P);
		this.mamAtestado.setIndImpresso(Boolean.FALSE);
		this.mamAtestado.setNroVias(Byte.valueOf("1"));
		this.mamAtestado.setDthrCriacao(new Date());
		this.mamAtestado.setAghCid(null);
		this.mamAtestado.setAcometidoDe(null);
		this.mamAtestado.setAipPacientes(this.consultaSelecionada.getPaciente());
		this.mamAtestado.setConsulta(consultaSelecionada);

		definirComposDeTexto(null, consultaSelecionada.getPaciente().getNome(), consultaSelecionada.getPaciente().getProntuario());
	}	
	
	public void radioSelecionado() {
		definirComposDeTexto(this.mamAtestado.getIndMotivoUsoFgts(), consultaSelecionada.getPaciente().getNome(), consultaSelecionada.getPaciente().getProntuario());
	}
	
	private void definirComposDeTexto(DominioIndMotivoUsoFgts indMotivoUsoFgts, String nome, Integer prontuario) {
		String[] textos = new String[4];
		textos = montarArrayTextos(indMotivoUsoFgts, nome, prontuario);
		
		this.textoPersonalizado1 = textos[0];
		this.textoPersonalizado2 = textos[1];
		this.dadosPacientePersonalizado1 = textos[2];
		this.dadosPacientePersonalizado2 = textos[3];
		this.nomeRadioSelecionado = textos[4];
		this.mamAtestado.setIndMotivoUsoFgts(indMotivoUsoFgts);
	}
	
	private String[] montarArrayTextos(DominioIndMotivoUsoFgts indMotivoUsoFgts, String nome, Integer prontuario) {
		String[] textos = new String[5];
		
		if(indMotivoUsoFgts == null){
			textos[0] = this.getBundle().getString("LABEL_ATESTADO_FGTS_ON01_ID05").replace(TAG_REPLACE_0, nome);
			textos[1] = this.getBundle().getString("LABEL_ATESTADO_FGTS_ON01_ID06").replace(TAG_REPLACE_0, prontuario.toString());
			textos[2] = this.getBundle().getString("LABEL_ATESTADO_FGTS_ON01_ID09");
			textos[3] = this.getBundle().getString("LABEL_ATESTADO_FGTS_ON01_ID10");
		} else if(DominioIndMotivoUsoFgts.PH.equals(indMotivoUsoFgts)){
			textos[0] = this.getBundle().getString("LABEL_ATESTADO_FGTS_ON02_ID05").replace(TAG_REPLACE_0, nome);
			textos[1] = this.getBundle().getString("LABEL_ATESTADO_FGTS_ON02_ID06").replace(TAG_REPLACE_0, prontuario.toString());
			textos[2] = this.getBundle().getString("LABEL_ATESTADO_FGTS_ON02_ID09");
			textos[3] = this.getBundle().getString("LABEL_ATESTADO_FGTS_ON02_ID10");
			textos[4] = indMotivoUsoFgts.getDescricao();
		} else if(DominioIndMotivoUsoFgts.NM.equals(indMotivoUsoFgts)){
			textos[0] = this.getBundle().getString("LABEL_ATESTADO_FGTS_ON03_ID05").replace(TAG_REPLACE_0, nome);
			textos[1] = this.getBundle().getString("LABEL_ATESTADO_FGTS_ON03_ID06").replace(TAG_REPLACE_0, prontuario.toString());
			textos[2] = this.getBundle().getString("LABEL_ATESTADO_FGTS_ON03_ID09");
			textos[3] = this.getBundle().getString("LABEL_ATESTADO_FGTS_ON03_ID10");
			textos[4] = indMotivoUsoFgts.getDescricao();
		} else if(DominioIndMotivoUsoFgts.DT.equals(indMotivoUsoFgts)){
			textos[0] = this.getBundle().getString("LABEL_ATESTADO_FGTS_ON04_ID05").replace(TAG_REPLACE_0, nome);
			textos[1] = this.getBundle().getString("LABEL_ATESTADO_FGTS_ON04_ID06").replace(TAG_REPLACE_0, prontuario.toString());
			textos[2] = this.getBundle().getString("LABEL_ATESTADO_FGTS_ON04_ID09");
			textos[3] = this.getBundle().getString("LABEL_ATESTADO_FGTS_ON04_ID10");
			textos[4] = indMotivoUsoFgts.getDescricao();
		}
		return textos;
	}
	
	private void recuperaParametro() {
		try {
			pTipoAtestado = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_ATESTADO_FGTS);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String montarAtestadoParte1Truncada(DominioIndMotivoUsoFgts indMotivoUsoFgts, String nome, Integer prontuario) {
		String texto = montarAtestadoParte1(indMotivoUsoFgts, nome, prontuario);
		if (texto.length() >= 30) {
			return texto.substring(0, 30);
		}else{
			return texto;
		}
	}	
	
	public String montarAtestadoParte1(DominioIndMotivoUsoFgts indMotivoUsoFgts, String nome, Integer prontuario) {
		String[] textos = new String[4];
		textos = montarArrayTextos(indMotivoUsoFgts, nome, prontuario);
		return textos[0].concat(" ").concat(textos[1]);
	}
	
	public String montarAtestadoParte2Truncada(DominioIndMotivoUsoFgts indMotivoUsoFgts, String nome, Integer prontuario) {
		String texto = montarAtestadoParte2(indMotivoUsoFgts, nome, prontuario);
		if (texto.length() >= 30) {
			return texto.substring(0, 30);
		}else{
			return texto;
		}
	}		
	
	public String montarAtestadoParte2(DominioIndMotivoUsoFgts indMotivoUsoFgts, String nome, Integer prontuario) {
		String[] textos = new String[4];
		textos = montarArrayTextos(indMotivoUsoFgts, nome, prontuario);
		return textos[2].concat(" ").concat(textos[3]);
	}
	
	public String montarAcometidoDe(DominioIndMotivoUsoFgts indMotivoUsoFgts, String acometidoDe, String estagioClinicoGeral) {
		 if(DominioIndMotivoUsoFgts.NM.equals(indMotivoUsoFgts)){
			 return acometidoDe;
		 }else{
			 return estagioClinicoGeral;
		 }
	}
	
	public String montarAtestadoAcometidoDeTruncada(DominioIndMotivoUsoFgts indMotivoUsoFgts, String acometidoDe, String estagioClinicoGeral) {
		String texto = montarAcometidoDe(indMotivoUsoFgts, acometidoDe, estagioClinicoGeral);
		if (texto.length() >= 30) {
			return texto.substring(0, 30).concat("...");
		}else{
			return texto;
		}
	}
	
	public boolean montarAtestadoAcometidoDeRenderizar(DominioIndMotivoUsoFgts indMotivoUsoFgts, String acometidoDe, String estagioClinicoGeral) {
		String texto = montarAcometidoDe(indMotivoUsoFgts, acometidoDe, estagioClinicoGeral);
		if (texto.length() >= 30) {
			return false;
		}else{
			return true;
		}
	}
	
	public boolean editandoRegistro(MamAtestados atestado){
		if(atestado.getSeq() !=null && atestado.equals(this.itemSelecionado)){
			return true;
		}
		return false;
	}
	
	/**
	 * SuggestionBox para listar todos cids ativos.
	 * @param filtro
	 * @return
	 */
	public List<AghCid> obterListaAghCid(String filtro) {
		return this.returnSGWithCount(aghuFacade.obterCids(filtro, true), aghuFacade.obterCidsCount(filtro, true));
	}	
	public String getNomeRadioSelecionado() {
		return nomeRadioSelecionado;
	}
	public void setNomeRadioSelecionado(String nomeRadioPersonalizado) {
		this.nomeRadioSelecionado = nomeRadioPersonalizado;
	}
	public String getTextoPersonalizado1() {
		return textoPersonalizado1;
	}
	public void setTextoPersonalizado1(String textoPersonalizado1) {
		this.textoPersonalizado1 = textoPersonalizado1;
	}
	public String getTextoPersonalizado2() {
		return textoPersonalizado2;
	}
	public void setTextoPersonalizado2(String textoPersonalizado2) {
		this.textoPersonalizado2 = textoPersonalizado2;
	}
	public String getDadosPacientePersonalizado1() {
		return dadosPacientePersonalizado1;
	}
	public void setDadosPacientePersonalizado1(String dadosPacientePersonalizado1) {
		this.dadosPacientePersonalizado1 = dadosPacientePersonalizado1;
	}
	public String getDadosPacientePersonalizado2() {
		return dadosPacientePersonalizado2;
	}
	public void setDadosPacientePersonalizado2(String dadosPacientePersonalizado2) {
		this.dadosPacientePersonalizado2 = dadosPacientePersonalizado2;
	}
	public List<AghCid> getListaCids() {
		return listaCids;
	}
	public void setListaCids(List<AghCid> listaCids) {
		this.listaCids = listaCids;
	}
	public List<MamAtestados> getMamAtestados() {
		return mamAtestados;
	}
	public void setMamAtestados(List<MamAtestados> mamAtestados) {
		this.mamAtestados = mamAtestados;
	}
	public MamAtestados getItemSelecionado() {
		return itemSelecionado;
	}
	public void setItemSelecionado(MamAtestados itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
	public MamAtestados getItemAux() {
		return itemAux;
	}

	public void setItemAux(MamAtestados itemAux) {
		this.itemAux = itemAux;
	}

	public boolean isModoEdicao() {
		return modoEdicao;
	}
	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}
	public MamAtestados getMamAtestado() {
		return mamAtestado;
	}
	public void setMamAtestado(MamAtestados mamAtestado) {
		this.mamAtestado = mamAtestado;
	}
	public AghParametros getpTipoAtestado() {
		return pTipoAtestado;
	}
	public void setpTipoAtestado(AghParametros pTipoAtestado) {
		this.pTipoAtestado = pTipoAtestado;
	}
	
	public AacConsultas getConsultaSelecionada() {
		return consultaSelecionada;
	}

	public void setConsultaSelecionada(AacConsultas consultaSelecionada) {
		this.consultaSelecionada = consultaSelecionada;
	}
}