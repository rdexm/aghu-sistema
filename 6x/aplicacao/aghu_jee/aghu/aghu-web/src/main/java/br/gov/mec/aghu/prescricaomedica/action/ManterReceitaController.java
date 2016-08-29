package br.gov.mec.aghu.prescricaomedica.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoPrescricaoReceituario;
import br.gov.mec.aghu.dominio.DominioTipoReceituario;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MamItemReceituario;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.VMamMedicamentos;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterReceitaController extends ActionController{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3272505263513538142L;
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	@EJB
	private IPacienteFacade pacienteFacade;
	@Inject
	private RelatorioConclusaoAbaReceituario relatorioConclusaoAbaReceituario;
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	private MamReceituarios receituarioGeral;
	private MamReceituarios receituarioEspecial;
	private List<MamItemReceituario> itemReceitaGeralList;
	private List<MamItemReceituario> itemReceitaEspecialList;
	private AacConsultas consulta;
	private Integer viasGeral;
	private Integer viasEspecial;
	private MamItemReceituario itemReceitaGeral;
	private MamItemReceituario itemReceitaEspecial;
	private VMamMedicamentos medicamentos;
	private Integer pacCodigo;
	private static final String DESCR_FORMULA = "FÓRMULA: ";
	private String voltarPara;
	private boolean edicaoGeral;
	private boolean edicaoEspecial;
	private boolean gravou=false;
	private MpmAltaSumario altaSumario = null;
	private enum ManterReceitaControllerExceptionCode implements BusinessExceptionCode{
		MSG_NRO_VIAS_REQUIRED;
	}
	
	//Parametros da tela de listaPacientesInternados
	private String atdSeq,prontuario,responsavel,leito,nome,idade,unidade;
	/**
	 * 
	 */
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio(){
		obterUnidadeFuncional();
		itemReceitaGeralList = new ArrayList<MamItemReceituario>();
		itemReceitaEspecialList = new ArrayList<MamItemReceituario>();
		iniciaReceitas();
		obterDadosReceituario();
		edicaoGeral = false;
		edicaoEspecial = false;
		gravou = false;
	}
	
	private void obterUnidadeFuncional() {
		AghAtendimentos atendimentos = prescricaoMedicaFacade.buscarAghAtendimento(Integer.parseInt(atdSeq));
		if(atendimentos != null && atendimentos.getUnidadeFuncional()!=null){
			unidade = atendimentos.getUnidadeFuncional().getDescricao();
		}
	}
	private void obterDadosReceituario(){
		List<MamReceituarios> geral = prescricaoMedicaFacade.buscarDadosReceituario(Integer.valueOf(atdSeq), DominioTipoReceituario.G);
		List<MamReceituarios> especial = prescricaoMedicaFacade.buscarDadosReceituario(Integer.valueOf(atdSeq), DominioTipoReceituario.E);
		if(!geral.isEmpty()){
			receituarioGeral = geral.get(0);
			itemReceitaGeralList = ambulatorioFacade.buscarItensReceita(receituarioGeral);
		}else{
			receituarioGeral = new MamReceituarios();
			receituarioGeral.setNroVias(Byte.valueOf("2"));
			receituarioGeral.setMpmAltaSumario(altaSumario);
			novoItemReceitaGeral();
		}
		if(!especial.isEmpty()){
			receituarioEspecial = especial.get(0);
			itemReceitaEspecialList = ambulatorioFacade.buscarItensReceita(receituarioEspecial);
			
		}else{
			receituarioEspecial = new MamReceituarios();
			receituarioEspecial.setNroVias(Byte.valueOf("2"));
			receituarioEspecial.setMpmAltaSumario(altaSumario);
			novoItemReceitaEspecial();
		}
	} 
	/**
	 * RF06 Carregar valores 
	 */
	private void preCarregar(){
		if(receituarioGeral != null){
			receituarioGeral.setAtendimentos(prescricaoMedicaFacade.obterAtendimentoPorSeq(Integer.parseInt(atdSeq)));
			receituarioGeral.setTipo(DominioTipoReceituario.G);
			receituarioGeral.setDthrCriacao(new Date());
			receituarioGeral.setPendente(DominioIndPendenteAmbulatorio.P);
			receituarioGeral.setIndImpresso(DominioSimNao.N); 
			receituarioGeral.setServidor(getServidorLogadoFacade().obterServidorLogado());
			receituarioGeral.setNroVias(obterNroViasGeral());
			receituarioGeral.setMpmAltaSumario(altaSumario);
			if(prontuario!= null){
				receituarioGeral.setPaciente(getPacienteFacade().obterPacientePorProntuario(Integer.parseInt(prontuario)));
			}
		}
		if(receituarioEspecial != null){
			receituarioEspecial.setAtendimentos(prescricaoMedicaFacade.obterAtendimentoPorSeq(Integer.parseInt(atdSeq)));
			receituarioEspecial.setTipo(DominioTipoReceituario.E);
			receituarioEspecial.setDthrCriacao(new Date());
			receituarioEspecial.setPendente(DominioIndPendenteAmbulatorio.P);
			receituarioEspecial.setIndImpresso(DominioSimNao.N);
			receituarioEspecial.setServidor(getServidorLogadoFacade().obterServidorLogado());
			receituarioEspecial.setNroVias(obterNroViasEspecial());
			receituarioEspecial.setMpmAltaSumario(altaSumario);
			if(prontuario!= null){
				receituarioEspecial.setPaciente(getPacienteFacade().obterPacientePorProntuario(Integer.parseInt(prontuario)));
			}
		}
	}
	public void gravar(){
		try {
			if(isItemReceitaGeralList() || isItemReceitaEspecialList()){
				validaNroVias();
				preCarregar();
				prescricaoMedicaFacade.inserirNovaReceita(receituarioGeral, itemReceitaGeralList,receituarioEspecial,itemReceitaEspecialList);
				apresentarMsgNegocio(Severity.INFO, "MSG_GRAVAR_SUCESSO_TELA_RECEITAS");
				obterDadosReceituario();
			}else{
				apresentarMsgNegocio(Severity.ERROR,"Obrigatório adicionar ao menos uma receita.");
			}
		}catch(BaseListException e){
			apresentarExcecaoNegocio(e);
		}
		catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());
		}catch (Exception e){
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());
		}
		
	}

	private boolean isItemReceitaEspecialList() {
		return !gravou && !itemReceitaEspecialList.isEmpty() && itemReceitaEspecialList != null;
	}

	private boolean isItemReceitaGeralList() {
		return !gravou && !itemReceitaGeralList.isEmpty() && itemReceitaGeralList != null;
	}
	
	public void inserirReceitaGeral(){
		itemReceitaGeralList.add(itemReceitaGeral);
		iniciaReceitas();
	}
	
	public void inserirReceitaEspecial(){
		itemReceitaEspecialList.add(itemReceitaEspecial);
		iniciaReceitas();
	}
	
	public void excluirItemReceita(Integer index,String tipo){
		
		if(tipo.equals("G")){
			itemReceitaGeralList.remove(index.intValue());
			novoItemReceitaGeral();
		}else if(tipo.equals("E")){
			itemReceitaEspecialList.remove(index.intValue());
			novoItemReceitaEspecial();
		}
	}
	

	public String excluirReceita(){
		if((receituarioGeral != null && receituarioGeral.getSeq() != null) || (receituarioEspecial!= null && receituarioEspecial.getSeq()!=null)){
			try{
				prescricaoMedicaFacade.excluirReceita(receituarioGeral, receituarioEspecial);
				apresentarMsgNegocio(Severity.INFO, "MSG_EXCLUIR_SUCESSO_TELA_RECEITAS");
				return cancelar();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}catch (Exception e){
				apresentarMsgNegocio(Severity.FATAL, "MSG_ERRO_OPERACAO", new Object[] {e.getMessage()});
			}
		}
		return null;
	}
	
	public void editarReceita(String tipo){
		if(tipo.equals("G")){
			edicaoGeral = true;
		}else{
			edicaoEspecial = true;
		}
	}
	public void confirmaEdicao(String tipo){
		if(tipo.equals("G")){
			edicaoGeral = false;
			int index = itemReceitaGeralList.indexOf(itemReceitaGeral);
			itemReceitaGeralList.remove(index);
			itemReceitaGeralList.add(index,itemReceitaGeral);
			novoItemReceitaGeral();
		}else{
			edicaoEspecial = false;
			int index = itemReceitaEspecialList.indexOf(itemReceitaEspecial);
			itemReceitaEspecialList.remove(index);
			itemReceitaEspecialList.add(index,itemReceitaEspecial);
			novoItemReceitaEspecial();
		}
		medicamentos=null; //limpar sugestionbox apos confirmar
	}
	public void cancelarEdicao(String tipo){
		medicamentos=null;
		if(tipo.equals("G")){
			edicaoGeral = false;
			novoItemReceitaGeral();
		}else{
			edicaoEspecial = false;
			novoItemReceitaEspecial();
		}
	}
	
	private void validaNroVias() throws BaseListException {
		BaseListException listaExcept = new BaseListException();
		if(  (receituarioGeral.getNroVias()== null || StringUtils.isEmpty(receituarioGeral.getNroVias().toString())) ||
			 (receituarioEspecial.getNroVias() == null || StringUtils.isEmpty(receituarioEspecial.getNroVias().toString()))){
			 listaExcept.add(new ApplicationBusinessException(ManterReceitaControllerExceptionCode.MSG_NRO_VIAS_REQUIRED));
		}
		if(listaExcept.hasException()){
			throw listaExcept;
		}
	}
	
	/**
	 * Consulta para preencher sugestion box medicamentos
	 * @param objPesquisa
	 */
	public List<VMamMedicamentos> obterMedicamentosReceitaVO(String objPesquisa){
		return this.returnSGWithCount(this.prescricaoMedicaFacade.obterMedicamentosReceitaPorDescricaoOuCodigo(objPesquisa),obterMedicamentosReceitaVOCount(objPesquisa));
	}
	
	public Long obterMedicamentosReceitaVOCount(String objPesquisa) {
		return this.prescricaoMedicaFacade.obterMedicamentosReceitaPorDescricaoOuCodigoCount(objPesquisa);
	}

	public String cancelar(){
		if(this.voltarPara != null){
			return this.voltarPara;
		}
		return "prescricaomedica-pesquisarListaPacientesInternados";
	}
	
	private void imprimirReceita(MamReceituarios receituario){
		boolean imprimiu=false;
		if(receituario.getIndImpresso()!= null && receituario.getIndImpresso().equals(DominioSimNao.S)){
			imprimiu = true;
		}
		try {
			relatorioConclusaoAbaReceituario.imprimirReceitaMedica(receituario.getSeq(), imprimiu);
			this.ambulatorioFacade.atualizarIndImpressaoReceituario(receituario.getSeq());
			} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				apresentarMsgNegocio(Severity.FATAL, "MSG_ERRO_OPERACAO", new Object[] {e.getMessage()});
		}
	}
	public void imprimir(){
		MamReceituarios geral=null;
		MamReceituarios especial= null;
		if(receituarioGeral != null && receituarioGeral.getSeq()!= null){
			geral = prescricaoMedicaFacade.obterReceituario(receituarioGeral.getSeq());	
		}
		if(receituarioEspecial != null && receituarioEspecial.getSeq()!=null){
			especial = prescricaoMedicaFacade.obterReceituario(receituarioEspecial.getSeq());	
		}
		if(geral == null && especial == null){
			apresentarMsgNegocio(Severity.INFO, "LABEL_MS01_TELA_RECEITA");
		}else{
			if(geral != null && geral.getSeq()!=null){
				imprimirReceita(geral);
			}
			if(especial != null && especial.getSeq()!=null){
				imprimirReceita(especial);
			}
		}
	}
	
	public void buscarReceita(){
		//TODO não mapeado
	}
	
	public Integer getSizeItensGeral() {
		return itemReceitaGeralList.size();
	}

	public Integer getSizeItensEspecial() {
		return itemReceitaEspecialList.size();
	}
	
	public void upItemReceitaGeral(Integer ordem,String tipo) {
		if(tipo.equals("G")){
			Collections.swap(itemReceitaGeralList, ordem, ordem-1);
		}else{
			Collections.swap(itemReceitaEspecialList, ordem, ordem-1);
		}
	}

	public void downItemReceitaGeral(Integer ordem, String tipo) {
		if (tipo.equals("G")) {
			Collections.swap(itemReceitaGeralList, ordem, ordem + 1);
		} else {
			Collections.swap(itemReceitaEspecialList, ordem, ordem + 1);
		}
	}
	
	// --[RECEITAS]
		private void iniciaReceitas() {
			medicamentos = null;
			gravou = false;
			novoItemReceitaGeral();
			novoItemReceitaEspecial();
		}
		public Byte obterNroViasGeral(){
			if(receituarioGeral!= null && receituarioGeral.getNroVias()!= null){
				return receituarioGeral.getNroVias();
			}else{
				return Byte.valueOf("2");
			}
		}
		public Byte obterNroViasEspecial(){
			if(receituarioEspecial!= null && receituarioEspecial.getNroVias()!=null){
				return receituarioEspecial.getNroVias();
			}else{
				return Byte.valueOf("2");
			}
		}
		
		private void novoItemReceitaGeral() {
			itemReceitaGeral = new MamItemReceituario();
			itemReceitaGeral.setTipoPrescricao(DominioTipoPrescricaoReceituario.M);
			itemReceitaGeral.setIndInterno(DominioSimNao.S);
			itemReceitaGeral.setFormaUso("");
			itemReceitaGeral.setNroGrupoImpressao(Byte.valueOf("1"));
			gravou = false;
		}

		private void novoItemReceitaEspecial() {
			itemReceitaEspecial = new MamItemReceituario();
			itemReceitaEspecial.setTipoPrescricao(DominioTipoPrescricaoReceituario.M);
			itemReceitaEspecial.setIndInterno(DominioSimNao.S);
			itemReceitaEspecial.setFormaUso("");
			itemReceitaEspecial.setNroGrupoImpressao(Byte.valueOf("1"));
			gravou = false;
		}

	public void verificaTipo(MamItemReceituario item) {
		
		if (DominioTipoPrescricaoReceituario.F.equals(item.getTipoPrescricao())) {
			item.setDescricao(DESCR_FORMULA);
			medicamentos=null;
		}
		else{
			item.setDescricao(null);
		}
	}
	
	public void atualizarDescricaoMedicamentoGeral() {
		atualizarDescricaoMedicamento(itemReceitaGeral, DominioTipoReceituario.G);
	}

	public void atualizarDescricaoMedicamentoEspecial() {
		atualizarDescricaoMedicamento(itemReceitaEspecial, DominioTipoReceituario.E);
	}

	public void atualizarDescricaoMedicamento(MamItemReceituario item, DominioTipoReceituario tipo) {
		if(medicamentos != null){
			StringBuilder descricao = new StringBuilder("");
			if (StringUtils.isNotBlank(medicamentos.getId().getDescricao())) {
				descricao.append(medicamentos.getId().getDescricao());
			}
			if (StringUtils.isNotBlank(medicamentos.getConcentracao().toString())) {
				descricao.append(' ').append(medicamentos.getConcentracao().toString());
			}
			if (medicamentos.getDescricaoUmm() != null && StringUtils.isNotBlank(medicamentos.getDescricaoUmm())) {
				descricao.append(' ').append(medicamentos.getDescricaoUmm());
			}
			item.setDescricao(descricao.toString());
			medicamentos = null;
		}
	}
	
	public String getItemReceitaDescricao(MamItemReceituario item) {
		StringBuffer str = new StringBuffer(32);
		if (StringUtils.isNotBlank(item.getDescricao())) {
			str.append(item.getDescricao());
		}
		return str.toString();
	}
	
	
	//GETTERS AND SETTERS

	public List<MamItemReceituario> getItemReceitaGeralList() {
		return itemReceitaGeralList;
	}

	public void setItemReceitaGeralList(List<MamItemReceituario> itemReceitaGeralList) {
		this.itemReceitaGeralList = itemReceitaGeralList;
	}

	public List<MamItemReceituario> getItemReceitaEspecialList() {
		return itemReceitaEspecialList;
	}

	public void setItemReceitaEspecialList(List<MamItemReceituario> itemReceitaEspecialList) {
		this.itemReceitaEspecialList = itemReceitaEspecialList;
	}

	public AacConsultas getConsulta() {
		return consulta;
	}

	public void setConsulta(AacConsultas consulta) {
		this.consulta = consulta;
	}

	public Integer getViasGeral() {
		return viasGeral;
	}

	public void setViasGeral(Integer viasGeral) {
		this.viasGeral = viasGeral;
	}

	public Integer getViasEspecial() {
		return viasEspecial;
	}

	public void setViasEspecial(Integer viasEspecial) {
		this.viasEspecial = viasEspecial;
	}

	public MamItemReceituario getItemReceitaGeral() {
		return itemReceitaGeral;
	}

	public void setItemReceitaGeral(MamItemReceituario itemReceitaGeral) {
		this.itemReceitaGeral = itemReceitaGeral;
	}

	public VMamMedicamentos getMedicamentos() {
		return medicamentos;
	}

	public void setMedicamentos(VMamMedicamentos medicamentos) {
		this.medicamentos = medicamentos;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(String atdSeq) {
		this.atdSeq = atdSeq;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public MamReceituarios getReceituarioGeral() {
		return receituarioGeral;
	}

	public void setReceituarioGeral(MamReceituarios receituarioGeral) {
		this.receituarioGeral = receituarioGeral;
	}

	public MamReceituarios getReceituarioEspecial() {
		return receituarioEspecial;
	}

	public void setReceituarioEspecial(MamReceituarios receituarioEspecial) {
		this.receituarioEspecial = receituarioEspecial;
	}

	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	public void setServidorLogadoFacade(IServidorLogadoFacade servidorLogadoFacade) {
		this.servidorLogadoFacade = servidorLogadoFacade;
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(
			IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
	}

	public IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}

	public void setAmbulatorioFacade(IAmbulatorioFacade ambulatorioFacade) {
		this.ambulatorioFacade = ambulatorioFacade;
	}

	public MamItemReceituario getItemReceitaEspecial() {
		return itemReceitaEspecial;
	}

	public void setItemReceitaEspecial(MamItemReceituario itemReceitaEspecial) {
		this.itemReceitaEspecial = itemReceitaEspecial;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public boolean isEdicaoGeral() {
		return edicaoGeral;
	}

	public void setEdicaoGeral(boolean edicaoGeral) {
		this.edicaoGeral = edicaoGeral;
	}

	public boolean isEdicaoEspecial() {
		return edicaoEspecial;
	}

	public void setEdicaoEspecial(boolean edicaoEspecial) {
		this.edicaoEspecial = edicaoEspecial;
	}

	
	public MpmAltaSumario getAltaSumario() {
		return altaSumario;
	}

	
	public void setAltaSumario(MpmAltaSumario altaSumario) {
		this.altaSumario = altaSumario;
	}
}