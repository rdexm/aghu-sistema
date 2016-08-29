package br.gov.mec.aghu.farmacia.dispensacao.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioCaracteristicaMicrocomputador;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.enums.ConselhoRegionalEnfermagemEnum;
import br.gov.mec.aghu.enums.ConselhoRegionalFarmaciaEnum;
import br.gov.mec.aghu.enums.ConselhoRegionalMedicinaEnum;
import br.gov.mec.aghu.enums.ConselhoRegionalOdontologiaEnum;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dispensacao.business.IFarmaciaDispensacaoFacade;
import br.gov.mec.aghu.farmacia.vo.MedicoResponsavelVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaPrescricaoMedicamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.vo.PacientesEmAtendimentoVO;
import net.sf.jasperreports.engine.JRException;


public class DispensacaoDePrescricaoNaoEletronicaController extends ActionController /*implements ActionPaginator*/ {

	private static final long serialVersionUID = -294428242675885215L;

	@EJB
	private IAdministracaoFacade administracaoFacade;
	
	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@EJB
	private IFarmaciaDispensacaoFacade farmaciaDispensacaoFacade;
	
	@Inject
	private DispMdtosCbPaginatorController dispMdtosCbPaginatorController;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	private Long seqAfaPrescricaoMedicamento;
	
	private AfaPrescricaoMedicamento prescricaoMedicamento;
	
	private AghUnidadesFuncionais unidadeFuncionalDoMicroDispensador;
	private AghUnidadesFuncionais unidadeFuncionalDoMicro;//Independente se dispensador
	private AghUnidadesFuncionais unidadeFuncionalDoAtendimento;
	
	private Boolean responsavelNaoCadastrado;
	private PacientesEmAtendimentoVO pacienteEmAtendimentoSelecionado;
	private MedicoResponsavelVO responsavel;
	
	private Boolean exibeDispensacaoMdtos;
	private String etiquetaDispensar;
	private AfaDispensacaoMdtos dispensacaoSemCB;
	private AfaDispensacaoMdtos dispensacaoSemCBOld;
	private List<AfaDispensacaoMdtos> listaDispensacoes;
	
	private String nomeComputadorRede;
	private RapServidores usuarioLogado;
	
	private Boolean origemInternacao;
	
	private Boolean dispencacaoComMdto;
	private Boolean exibeModal;
	
	private Boolean gerarNumeroExternoAutomatico;
	
	private Integer pGrFarmIndustrial;
	private Integer pGrMatMedic;
	
	private String erroProcessarEtiqueta;
	
	private List<String> conselhosValidos = new ArrayList<String>();
	
	
	public void iniciarPagina(){
		this.erroProcessarEtiqueta = null;

		if(conselhosValidos.isEmpty()){
			conselhosValidos.addAll(ConselhoRegionalFarmaciaEnum.getListaValores());
			conselhosValidos.addAll(ConselhoRegionalOdontologiaEnum.getListaValores());
			conselhosValidos.addAll(ConselhoRegionalEnfermagemEnum.getListaValores());
			conselhosValidos.addAll(ConselhoRegionalMedicinaEnum.getListaValores());
		}
		
		buscarMicroDispensador();
		dispensacaoSemCB = new AfaDispensacaoMdtos();
		dispensacaoSemCB.setPermiteDispensacaoSemEtiqueta(Boolean.TRUE);
		origemInternacao = !DominioOrigemAtendimento.A.equals(pacienteEmAtendimentoSelecionado.getOrigem()); 
		unidadeFuncionalDoAtendimento = aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(pacienteEmAtendimentoSelecionado.getUnfSeq());
		etiquetaDispensar = null;
		responsavelNaoCadastrado = Boolean.FALSE;
		
		try{
			usuarioLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		//seqAfaPrescricaoMedicamento = 61l;
		if(seqAfaPrescricaoMedicamento != null){
			prescricaoMedicamento = farmaciaFacade.obterAfaPrescricaoMedicamento(seqAfaPrescricaoMedicamento, true,
					AfaPrescricaoMedicamento.Fields.CONSELHO_PROFISSIONAL, AfaPrescricaoMedicamento.Fields.ATENDIMENTO,
					AfaPrescricaoMedicamento.Fields.ATENDIMENTO_PACIENTE);
			if(prescricaoMedicamento.getServidorResponsavel() != null){
				responsavel = farmaciaFacade.pesquisarMedicoResponsavel(null,
						prescricaoMedicamento.getServidorResponsavel().getId().getMatricula(),
						prescricaoMedicamento.getServidorResponsavel().getId().getVinCodigo()).get(0);
			}
			buscaDispensacoesDaPrescricao();
			exibeDispensacaoMdtos = Boolean.TRUE;
		}else{
			prescricaoMedicamento = new AfaPrescricaoMedicamento();
			prescricaoMedicamento.setDtReferencia(new Date());
			listaDispensacoes = new ArrayList<AfaDispensacaoMdtos>();
			responsavel = null;
			exibeDispensacaoMdtos = Boolean.FALSE;
		}
		processarDadosImpressaoTicket();
		exibeModal = true;
		try{
			String paramNumExterno = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_GERAR_NUMERO_EXTERNO_AUTO);
			gerarNumeroExternoAutomatico = "S".equals(paramNumExterno.trim());
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			gerarNumeroExternoAutomatico = Boolean.FALSE;
		}
		
		if(pGrFarmIndustrial == null || pGrMatMedic == null){
			pGrFarmIndustrial = parametroFacade.obterValorNumericoAghParametros(AghuParametrosEnum.P_GR_FARM_INDUSTRIAL.toString()).intValue();
			pGrMatMedic = parametroFacade.obterValorNumericoAghParametros(AghuParametrosEnum.P_GR_MAT_MEDIC.toString()).intValue();
		}
	
	}

	private void processarDadosImpressaoTicket() {
		try{
			Object[] microENome = farmaciaDispensacaoFacade.obterNomeUnidFuncComputadorDispMdtoCb(null, nomeComputadorRede);
			AghUnidadesFuncionais unidadeFuncionalMicro = (AghUnidadesFuncionais) microENome[0];
			dispMdtosCbPaginatorController.setUnidadeFuncionalMicro(unidadeFuncionalMicro);
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		dispMdtosCbPaginatorController.instanciaParametrosModalTicket();
		
		dispMdtosCbPaginatorController.setAtdSeqPrescricao(pacienteEmAtendimentoSelecionado.getAtdSeq());
		dispMdtosCbPaginatorController.setSeqPrescricao(null);
		dispMdtosCbPaginatorController.setPmmSeq(prescricaoMedicamento.getSeq());
		dispMdtosCbPaginatorController.setDataUltimaImpressao(null);
		dispMdtosCbPaginatorController.setPrescricaoMedicamento(prescricaoMedicamento);
		dispencacaoComMdto = dispMdtosCbPaginatorController.getDispencacaoComMdto();
		if(dispMdtosCbPaginatorController.getExibeModal() != null){
			exibeModal = dispMdtosCbPaginatorController.getExibeModal();
		}else{
			exibeModal = Boolean.FALSE;
		}
	}

	private void buscaDispensacoesDaPrescricao() {
		listaDispensacoes = farmaciaDispensacaoFacade
				.pesquisarDispensacaoMdtoPorPrescricaoNaoEletronica(prescricaoMedicamento
						.getSeq(), null);
		farmaciaDispensacaoFacade.refresh(listaDispensacoes);
	}

	private void buscarMicroDispensador() {
		try {
			nomeComputadorRede =  getEnderecoRedeHostRemoto();
			AghMicrocomputador micro = administracaoFacade.obterAghMicroComputadorPorNomeOuIP(nomeComputadorRede, null);
			if(micro != null){
				unidadeFuncionalDoMicro = micro.getAghUnidadesFuncionais();
				if(unidadeFuncionalDoMicro != null && unidadeFuncionalDoMicro.getAlmoxarifado() != null){
					unidadeFuncionalDoMicro.getAlmoxarifado().getSeqDescricao();//resolve lazy
				}
			}
			AghMicrocomputador microDispensador = administracaoFacade.obterAghMicroComputadorPorNomeOuIP(nomeComputadorRede, DominioCaracteristicaMicrocomputador.DISPENSADOR);
			if(microDispensador != null){
				unidadeFuncionalDoMicroDispensador = microDispensador.getAghUnidadesFuncionais();
			}else{
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_COMPUTADOR_NAO_POSSUI_PERMISSAO_DISPENSAR");
			}
		} catch (UnknownHostException e) {
			apresentarExcecaoNegocio(new ApplicationBusinessException(ApplicationBusinessExceptionCode.NAO_FOI_POSSIVEL_RECUPERAR_COMPUTADOR));
		}
	}
	
	public List<MedicoResponsavelVO> pesquisarResponsaveis(String objPesquisa){
		String strPesquisa = objPesquisa != null ? objPesquisa : null;
		return this.returnSGWithCount(farmaciaFacade.pesquisarMedicoResponsavel(strPesquisa, null, null),pesquisarMedicoResponsavelCount(objPesquisa));
	}
	
	public Long pesquisarMedicoResponsavelCount(String objPesquisa){
		String strPesquisa = objPesquisa != null ? objPesquisa : null;
		return aghuFacade.obterProfessoresInternacaoTodosCount(strPesquisa, null, null);
	}
	
	public List<RapConselhosProfissionais> pesquisarSiglas(String strPesquisa){
		if(CoreUtil.isNumeroShort(strPesquisa)){
			Short pesquisaInt = CoreUtil.isNumeroShort(strPesquisa) ? Short.valueOf(strPesquisa) : null;
			return this.returnSGWithCount(cadastrosBasicosFacade.pesquisarConselhosProfissionais(pesquisaInt,
					null, null, DominioSituacao.A, null, null, 0,
					100, null, true, conselhosValidos),pesquisarSiglasCount(strPesquisa));
		}else{
			String pesquisa = strPesquisa != null ? strPesquisa : null;
			return cadastrosBasicosFacade.pesquisarConselhosProfissionais(null,
					null, pesquisa, DominioSituacao.A, null, null, 0,
					100, null, true, conselhosValidos);
		}
	}
	
	public Long pesquisarSiglasCount(String strPesquisa){
		if(CoreUtil.isNumeroShort(strPesquisa)){
			Short pesquisaInt = CoreUtil.isNumeroShort(strPesquisa) ? Short.valueOf(strPesquisa) : null;
			return cadastrosBasicosFacade.pesquisarConselhosProfissionaisCount(pesquisaInt,
					null, null, DominioSituacao.A, null, null,conselhosValidos);
		}else{
			String pesquisa = strPesquisa != null ? strPesquisa : null;
			return cadastrosBasicosFacade.pesquisarConselhosProfissionaisCount(null,
					null, pesquisa, DominioSituacao.A, null, null,conselhosValidos);
		}
	}
	
	public void limpar() {
		this.prescricaoMedicamento = null;
		this.responsavel = null;
		this.erroProcessarEtiqueta = null;
		this.responsavelNaoCadastrado = Boolean.FALSE;
	}
	
	public String cancelar(){
		limpar();
		return "pesquisarPacientesEmAtendimentoList";
	}
	
	public String encaminharTelaSituacaoDispensacao(){
		return "farmacia-medicamento-situacao-dispensacao";
	}
	
	public void gravarDispensacaoNaoEletronica(){
		//Setar responsáel a partir de ProfessorCrmInternacaoVO
		try{
			AghAtendimentos atendimento = aghuFacade.buscarAtendimentoPorSeq(pacienteEmAtendimentoSelecionado.getAtdSeq());
			prescricaoMedicamento.setAtendimento(atendimento);
			prescricaoMedicamento.setServidor(usuarioLogado);
			if(responsavel != null){
				RapServidores servidorResponsavel = registroColaboradorFacade.buscarServidor(responsavel.getVinCodigo(), responsavel.getMatricula());
				prescricaoMedicamento.setServidorResponsavel(servidorResponsavel);
			}
			farmaciaDispensacaoFacade.persistirAfaPrescricaoMedicamento(prescricaoMedicamento);
			//farmaciaDispensacaoFacade.flush();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PRESCRICAO_NAO_ELETRONICA_INSERIDA_SUCESSO");
			exibeDispensacaoMdtos = Boolean.TRUE;
			processarDadosImpressaoTicket();
			seqAfaPrescricaoMedicamento = prescricaoMedicamento.getSeq();
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}/* catch (AGHUNegocioException e) {
			apresentarExcecaoNegocio(e);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}*/
	}
	
	public void dispensarMdtoSemCB(){
		try {
			this.erroProcessarEtiqueta = null;
			String descricaoMdto = dispensacaoSemCB.getMedicamento().getDescricao();
			farmaciaDispensacaoFacade
					.persistirAfaDispMdtoComPrescricaoNaoEletronica(
							dispensacaoSemCB.getMedicamento().getMatCodigo(),
							dispensacaoSemCB.getQtdeDispensada(),
							dispensacaoSemCB.getObservacao(), 
							prescricaoMedicamento.getSeq(),
							pacienteEmAtendimentoSelecionado.getAtdSeq(),
							nomeComputadorRede, usuarioLogado,
							unidadeFuncionalDoMicroDispensador.getSeq(),
							pacienteEmAtendimentoSelecionado.getUnfSeq());
			//farmaciaDispensacaoFacade.flush();
			buscaDispensacoesDaPrescricao();
			dispensacaoSemCB = new AfaDispensacaoMdtos();
			dispensacaoSemCB.setPermiteDispensacaoSemEtiqueta(Boolean.TRUE);
			apresentarMsgNegocio(Severity.INFO, "MSG_MDTO_DISPENSADO_SUCESSO", descricaoMdto);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		
	}
	
	public void alterarDispensacaoSemCB(){
		try {
			farmaciaDispensacaoFacade
					.atualizarAfaDispMdtoComPrescricaoNaoEletronica(
							dispensacaoSemCBOld,
							dispensacaoSemCB.getQtdeDispensada(),
							dispensacaoSemCB.getObservacao(),
							nomeComputadorRede, usuarioLogado, !dispensacaoSemCB.getPermiteDispensacaoSemEtiqueta());
			//farmaciaDispensacaoFacade.flush();
			buscaDispensacoesDaPrescricao();
			dispensacaoSemCB = new AfaDispensacaoMdtos();
			dispensacaoSemCB.setPermiteDispensacaoSemEtiqueta(Boolean.TRUE);
			apresentarMsgNegocio(Severity.INFO, "MESSAGE_DISPENSACAO_PMM_ALTERADA_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			buscaDispensacoesDaPrescricao();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void selecionaDispensacaoParaEdicao(final AfaDispensacaoMdtos dispensacaoSelecionada){
		try {
			dispensacaoSemCBOld = farmaciaFacade.getAfaDispOldDesatachado(dispensacaoSelecionada);
			dispensacaoSemCB = farmaciaFacade.getAfaDispOldDesatachado(dispensacaoSelecionada);
			etiquetaDispensar = "";
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void dispensarMdtoComCB(){
		try {
			String nroEtiquetaFormatada = farmaciaDispensacaoFacade.validarCodigoBarrasEtiqueta(etiquetaDispensar);
			farmaciaDispensacaoFacade.
				dispensaMdtoComCBPrescricaoNaoEletronica(
					nroEtiquetaFormatada, pacienteEmAtendimentoSelecionado.getAtdSeq(),nomeComputadorRede, usuarioLogado,
					unidadeFuncionalDoMicroDispensador.getSeq(),
					pacienteEmAtendimentoSelecionado.getUnfSeq(), prescricaoMedicamento.getSeq());
			//farmaciaDispensacaoFacade.flush();
			buscaDispensacoesDaPrescricao();
			apresentarMsgNegocio(Severity.INFO, "MESSAGE_DISP_MDTOS_ETQ_SUCESSO");
			etiquetaDispensar = "";
			this.erroProcessarEtiqueta = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			etiquetaDispensar = null;
			this.erroProcessarEtiqueta = WebUtil.initLocalizedMessage(e.getMessage(), null);
		}  catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			this.erroProcessarEtiqueta = WebUtil.initLocalizedMessage(e.getMessage(), null);
		}
		
	}
	
	public void efetuarEstornoDispensacao(){
		try {
			farmaciaDispensacaoFacade.efetuarEstornoDispensacaoMdtoNaoEletronica(dispensacaoSemCBOld,nomeComputadorRede, usuarioLogado);
			buscaDispensacoesDaPrescricao();
			//farmaciaDispensacaoFacade.flush();
			apresentarMsgNegocio(Severity.INFO, "MESSAGE_ESTORNO_LISTA_SUCESSO");
			dispensacaoSemCBOld = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void selecionaDispensacaoParaEstorno(final AfaDispensacaoMdtos dispensacaoSelecionada){
		try {
			dispensacaoSemCBOld = farmaciaFacade.getAfaDispOldDesatachado(dispensacaoSelecionada);
			dispensacaoSemCB = new AfaDispensacaoMdtos();
			dispensacaoSemCB.setPermiteDispensacaoSemEtiqueta(Boolean.TRUE);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void limpaSelecaoResponsavel(){
		if(responsavelNaoCadastrado){
			responsavel = null;
		}else{
			prescricaoMedicamento.setConselhoProfissional(null);
			prescricaoMedicamento.setNomeResponsavel(null);
		}
	}
	
	public List<AfaMedicamento> pesquisarMedicamentos(String objPesquisa){
		List<ScoMaterial> listaMaterial = this.comprasFacade
				.listarScoMateriaisPorGrupoEspecifico(objPesquisa, 0, 100,
						null, true, pGrFarmIndustrial, pGrMatMedic);
		
		List<AfaMedicamento> listaMedicamento = new ArrayList<AfaMedicamento>();
		for(ScoMaterial material : listaMaterial){
			listaMedicamento.add(material.getAfaMedicamento());
		}
		return this.returnSGWithCount(listaMedicamento,pesquisarMedicamentosCount(objPesquisa));
	}
	
	public Long pesquisarMedicamentosCount(String objPesquisa){
		return this.comprasFacade.listarScoMateriaisPorGrupoEspecificoCount(objPesquisa, pGrFarmIndustrial, pGrMatMedic);
	}
	
//	Removido na Melhoria em Desenvolvimento #37437
//	public void processarDataInicioValidade(){
//		if(unidadeFuncionalDoAtendimento.getHrioValidadePme() == null ||
//				DateUtil.validaHoraMaior(unidadeFuncionalDoAtendimento.getHrioValidadePme(), getPrescricaoMedicamento().getDthrInicio())){
//			getPrescricaoMedicamento().setDtReferencia(getPrescricaoMedicamento().getDthrInicio());
//		}else{
//			getPrescricaoMedicamento().setDtReferencia(DateUtil.adicionaDias(getPrescricaoMedicamento().getDthrInicio(), 1));
//		}
//	}
//		
//		<a:support event="oninputblur"  reRender="dataConsulta,dataReferencia" ajaxSingle="true" 
//			action="#{dispensacaoDePrescricaoNaoEletronicaController.processarDataInicioValidade}"
//			rendered="#{dispensacaoDePrescricaoNaoEletronicaController.origemInternacao}" />
//		<a:support event="onchanged" reRender="dataConsulta,dataReferencia" ajaxSingle="true" 
//			action="#{dispensacaoDePrescricaoNaoEletronicaController.processarDataInicioValidade}"
//			rendered="#{dispensacaoDePrescricaoNaoEletronicaController.origemInternacao}" />
	
	public void processarImpressaoTicket(){
		dispMdtosCbPaginatorController.carregarListaImpressaoTicket();
		exibeModal = dispMdtosCbPaginatorController.getExibeModal();
	}
	
	public void imprimirTicketRaw() throws JRException, SystemException, IOException, SistemaImpressaoException, ApplicationBusinessException{
		dispMdtosCbPaginatorController.imprimirTicket();
	}
	
	public String getBackGroundColor(AfaDispensacaoMdtos dispensacao){
		if(dispensacao != null && dispensacao.getQtdeDispensada().equals(dispensacao.getQtdeEstornada())){
			return "background-color:#FF9999";
		}else{
			return "";
		}
	}
	
	public void limpaQtdeMdoDispensado(){
		getDispensacaoSemCB().setQtdeDispensada(null);
		getDispensacaoSemCB().setObservacao(null);
	}
	
	public AfaPrescricaoMedicamento getPrescricaoMedicamento() {
		return prescricaoMedicamento;}
	public void setPrescricaoMedicamento(
			AfaPrescricaoMedicamento prescricaoMedicamento) {
		this.prescricaoMedicamento = prescricaoMedicamento;}
	public AghUnidadesFuncionais getUnidadeFuncionalDoMicroDispensador() {
		return unidadeFuncionalDoMicroDispensador;
	}
	public void setUnidadeFuncionalDoMicroDispensador(
			AghUnidadesFuncionais unidadeFuncionalDoMicroDispensador) {
		this.unidadeFuncionalDoMicroDispensador = unidadeFuncionalDoMicroDispensador;}
	public Boolean getResponsavelNaoCadastrado() {
		return responsavelNaoCadastrado;}
	public void setResponsavelNaoCadastrado(Boolean responsavelNaoCadastrado) {
		this.responsavelNaoCadastrado = responsavelNaoCadastrado;
	}
	public PacientesEmAtendimentoVO getPacienteEmAtendimentoSelecionado() {
		return pacienteEmAtendimentoSelecionado;}
	public void setPacienteEmAtendimentoSelecionado(
			PacientesEmAtendimentoVO pacienteEmAtendimentoSelecionado) {
		this.pacienteEmAtendimentoSelecionado = pacienteEmAtendimentoSelecionado;}
	public String getEtiquetaDispensar() {
		return etiquetaDispensar;}
	public void setEtiquetaDispensar(String etiquetaDispensar) {
		this.etiquetaDispensar = etiquetaDispensar;}
	public MedicoResponsavelVO getResponsavel() {
		return responsavel;	}
	public Boolean getExibeDispensacaoMdtos() {
		return exibeDispensacaoMdtos;	}
	public void setResponsavel(MedicoResponsavelVO responsavel) {
		this.responsavel = responsavel;	}
	public void setExibeDispensacaoMdtos(Boolean exibeDispensacaoMdtos) {
		this.exibeDispensacaoMdtos = exibeDispensacaoMdtos;	}
	public AfaDispensacaoMdtos getDispensacaoSemCB() {
		return dispensacaoSemCB;	}
	public void setDispensacaoSemCB(AfaDispensacaoMdtos dispensacaoSemCB) {
		this.dispensacaoSemCB = dispensacaoSemCB;	}
	public List<AfaDispensacaoMdtos> getListaDispensacoes() {
		return listaDispensacoes;	}
	public void setListaDispensacoes(List<AfaDispensacaoMdtos> listaDispensacoes) {
		this.listaDispensacoes = listaDispensacoes;	}
	public Long getSeqAfaPrescricaoMedicamento() {
		return seqAfaPrescricaoMedicamento;	}
	public String getNomeComputadorRede() {
		return nomeComputadorRede;	}
	public void setSeqAfaPrescricaoMedicamento(Long seqAfaPrescricaoMedicamento) {
		this.seqAfaPrescricaoMedicamento = seqAfaPrescricaoMedicamento;	}
	public void setNomeComputadorRede(String nomeComputadorRede) {
		this.nomeComputadorRede = nomeComputadorRede;	}
	public RapServidores getUsuarioLogado() {
		return usuarioLogado;	}
	public void setUsuarioLogado(RapServidores usuarioLogado) {
		this.usuarioLogado = usuarioLogado;	}
	public AghUnidadesFuncionais getUnidadeFuncionalDoMicro() {
		return unidadeFuncionalDoMicro;	}
	public void setUnidadeFuncionalDoMicro(
			AghUnidadesFuncionais unidadeFuncionalDoMicro) {
		this.unidadeFuncionalDoMicro = unidadeFuncionalDoMicro;	}
	public AfaDispensacaoMdtos getDispensacaoSemCBOld() {
		return dispensacaoSemCBOld;	}
	public void setDispensacaoSemCBOld(AfaDispensacaoMdtos dispensacaoSemCBOld) {
		this.dispensacaoSemCBOld = dispensacaoSemCBOld;	}
	public Boolean getOrigemInternacao() {
		return origemInternacao;	}
	public void setOrigemInternacao(Boolean origemInternacao) {
		this.origemInternacao = origemInternacao;	}
	public AghUnidadesFuncionais getUnidadeFuncionalDoAtendimento() {
		return unidadeFuncionalDoAtendimento;	}
	public void setUnidadeFuncionalDoAtendimento(
			AghUnidadesFuncionais unidadeFuncionalDoAtendimento) {
		this.unidadeFuncionalDoAtendimento = unidadeFuncionalDoAtendimento;	}

	public Boolean getDispencacaoComMdto() {
		return dispencacaoComMdto;
	}

	public void setDispencacaoComMdto(Boolean dispencacaoComMdto) {
		this.dispencacaoComMdto = dispencacaoComMdto;
	}

	public void setExibeModal(Boolean exibeModal) {
		this.exibeModal = exibeModal;
	}

	public Boolean getExibeModal() {
		return exibeModal;
	}
	
	public String getErroProcessarEtiqueta() {
		return erroProcessarEtiqueta;
	}

	public void setErroProcessarEtiqueta(String erroProcessarEtiqueta) {
		this.erroProcessarEtiqueta = erroProcessarEtiqueta;
	}

	public Boolean getGerarNumeroExternoAutomatico() {
		return gerarNumeroExternoAutomatico;
	}

	public void setGerarNumeroExternoAutomatico(Boolean gerarNumeroExternoAutomatico) {
		this.gerarNumeroExternoAutomatico = gerarNumeroExternoAutomatico;
	}
}