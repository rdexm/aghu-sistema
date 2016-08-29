package br.gov.mec.aghu.prescricaomedica.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.TabChangeEvent;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioEscoreBilirrubina;
import br.gov.mec.aghu.dominio.DominioEscoreCardiovascular;
import br.gov.mec.aghu.dominio.DominioEscoreConcentracao;
import br.gov.mec.aghu.dominio.DominioEscoreCreatinina;
import br.gov.mec.aghu.dominio.DominioEscoreDigestivo;
import br.gov.mec.aghu.dominio.DominioEscoreEstadoCirurgico;
import br.gov.mec.aghu.dominio.DominioEscoreFrequencia;
import br.gov.mec.aghu.dominio.DominioEscoreGlasglow;
import br.gov.mec.aghu.dominio.DominioEscoreHepatico;
import br.gov.mec.aghu.dominio.DominioEscoreIdade;
import br.gov.mec.aghu.dominio.DominioEscoreLeucocitos;
import br.gov.mec.aghu.dominio.DominioEscoreLocalizacao;
import br.gov.mec.aghu.dominio.DominioEscoreNeurologico;
import br.gov.mec.aghu.dominio.DominioEscoreOxigenacao;
import br.gov.mec.aghu.dominio.DominioEscorePermanencia;
import br.gov.mec.aghu.dominio.DominioEscorePlanejado;
import br.gov.mec.aghu.dominio.DominioEscorePlaquetas;
import br.gov.mec.aghu.dominio.DominioEscorePressao;
import br.gov.mec.aghu.dominio.DominioEscoreTemperatura;
import br.gov.mec.aghu.dominio.DominioEscoreTipoCirurgia;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoSaps3;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmEscoreSaps3;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class PreencherEscoreGravidadeController extends ActionController {
	private static final long serialVersionUID = -5696986976905765981L;
	private static final String TITLE1 = "Demogr\u00E1fico / estado pr\u00E9vio de sa\u00FAde";
	private static final String TITLE2 = "Categoria Diagn\u00F3stica";
	private static final String TITLE3 = "Vari\u00E1veis fisiol\u00F3gicas na admiss\u00E3o";
	private static final String VAZIO = "&nbsp;";
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	@EJB
	private IParametroFacade parametroFacade;
	@Inject
	private VerificarPrescricaoMedicaController verificarPrescricaoMedicaController;
	private AipPacientes paciente;
	private Integer atdSeq;
	private Integer seq;
	private String prontuarioFormatado;
	private String nomePaciente;
	private AghAtendimentos atendimento;
	private Integer activeTab;
	private String titleAba1 = VAZIO;
	private String titleAba2 = VAZIO;
	private String titleAba3 = VAZIO;
	private MpmEscoreSaps3 saps3;
	private  DominioEscoreIdade idade;
	private  DominioEscorePermanencia permanencia;
	private  DominioEscoreLocalizacao localizacao;
	private  DominioSimNao tratCancer;
	private  DominioSimNao cancerHemo;
	private  DominioSimNao cirrose;
	private  DominioSimNao cancer;
	private  DominioSimNao icc;
	private  DominioSimNao aids;
	private  DominioSimNao terap;
	private Short tratCancerValor = 3;
	private Short cancerHemoValor = 6;
	private Short cirroseValor = 8;
	private Short cancerValor = 11;
	private Short iccValor = 6;
	private Short aidsValor = 8;
	private Short terapValor = 3;
	private  DominioEscorePlanejado tipoAdmissaoUti;
	private  DominioEscoreCardiovascular cardiovascular;
	private  DominioEscoreDigestivo digestivo;
	private  DominioEscoreHepatico hepatico;
	private  DominioEscoreNeurologico neurologico;
	private  DominioEscoreEstadoCirurgico estadoCirurgico;
	private  DominioEscoreTipoCirurgia tipoCirurgia;
	private  DominioSimNao nasocomial;
	private  DominioSimNao respiratoria;
	private Short nasocomialValor = 4;
	private Short respiratoriaValor = 5;
	private DominioEscoreGlasglow glasglow;
	private DominioEscoreBilirrubina bilirrubina;
	private DominioEscoreTemperatura temperatura;
	private DominioEscoreCreatinina creatinina;
	private DominioEscoreFrequencia frequencia;
	private DominioEscoreLeucocitos leucocitos;
	private DominioEscoreConcentracao concentracao;
	private DominioEscorePlaquetas plaquetas;
	private DominioEscorePressao pressao;
	private DominioEscoreOxigenacao oxigenacao; 
	private Short pontosSaps;
	private Double somaObito = 20.5958;
	private Double multiObito = 7.3068;
	private Double subObito = -32.6659;
	private Double obito;
	private Double somaObitoCustom = 71.0599;
	private Double multiObitoCustom = 13.2322;
	private Double subObitoCustom = -64.5990;
	private Double obitoCustom;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
		titleAba1 = TITLE1;
		titleAba2 = TITLE2;
		titleAba3 = TITLE3;
	}
	public void iniciar()  {
		pontosSaps = null;
		saps3 = null;
		saps3 = prescricaoMedicaFacade.obterEscoreSaps3(seq);
		atdSeq = saps3.getAtendimento().getSeq();
		List<AipPacientes> listaPacientes  = prescricaoMedicaFacade.pesquisarPacientePorAtendimento(atdSeq);
		if(listaPacientes != null){
			if(listaPacientes.size() > 0){
				paciente = listaPacientes.get(0);
				nomePaciente = listaPacientes.get(0).getNome();
				prontuarioFormatado = CoreUtil.formataProntuario(listaPacientes.get(0).getProntuario());
				populaIdade();
				atendimento = prescricaoMedicaFacade.obterAtendimentoPorSeq(atdSeq);
				populaPermanencia();
				if(saps3 != null){
					setarCamposAba1(); setarCamposAba2(); setarCamposAba3();
				}
				Long plaquetasParam = null;
				Long creatininaParam = null;
				Long bilirrubinaParam = null;
				Long LeucocitosParam = null;
				Long concentracaoParam = null;
				try {
					plaquetasParam = prescricaoMedicaFacade.popularResultadosParamSaps3(parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_EXAME_PLAQUETAS_SAPS3),atdSeq);
					creatininaParam = prescricaoMedicaFacade.popularResultadosParamSaps3(parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_EXAME_CREATININA_SAPS3),atdSeq);
					bilirrubinaParam = prescricaoMedicaFacade.popularResultadosParamSaps3(parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_EXAME_BILIRRUBINAS_TOTAIS_SAPS3),atdSeq);
					LeucocitosParam = prescricaoMedicaFacade.popularResultadosParamSaps3(parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_EXAME_LEUCOCITOS_SAPS3),atdSeq);
					concentracaoParam = prescricaoMedicaFacade.popularResultadosParamSaps3(parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_EXAME_GASOMETRIA_ARTERIAL_SAPS3),atdSeq);
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
				if(plaquetasParam != null){
					popularPlaquetas(plaquetasParam);
				}if(creatininaParam != null){
					popularCreatinina(creatininaParam);
				}if(creatininaParam != null){
					popularBilirrubina(bilirrubinaParam);
				}if(LeucocitosParam != null){
					popularLeucocitos(LeucocitosParam);
				}if(concentracaoParam != null){
					popularConcentracao(concentracaoParam);
				}
				calcular();
			}
		}
	}
	public String voltar(){
		limparCampos();
		return "prescricaomedica-verificaPrescricaoMedica";
	}
	public void limparCampos() {
		paciente = null;
		atdSeq = null;
		seq = null;
		prontuarioFormatado = null;
		nomePaciente = null;
		atendimento = null;
		activeTab = null;
		saps3 = null;
		idade = null;
		permanencia = null;
		localizacao = null;
		tratCancer = null;
		cancerHemo = null;
		cirrose = null;
		cancer = null;
		icc = null;
		aids = null;
		terap = null;
		tratCancerValor = 3;
		cancerHemoValor = 6;
		cirroseValor = 8;
		cancerValor = 11;
		iccValor = 6;
		aidsValor = 8;
		terapValor = 3;
		tipoAdmissaoUti = null;
		cardiovascular = null;
		digestivo = null;
		hepatico = null;
		neurologico = null;
		estadoCirurgico = null;
		tipoCirurgia = null;
		nasocomial = null;
		respiratoria = null;
		nasocomialValor = 4;
		respiratoriaValor = 5;
		glasglow = null;
		bilirrubina = null;
		temperatura = null;
		creatinina = null;
		frequencia = null;
		leucocitos = null;
		concentracao = null;
		plaquetas = null;
		pressao = null;
		oxigenacao = null;
		pontosSaps = null;
		somaObito = 20.5958;
		multiObito = 7.3068;
		subObito = -32.6659;
		obito = null;
		somaObitoCustom = 71.0599;
		multiObitoCustom = 13.2322;
		subObitoCustom = -64.5990;
		obitoCustom = null;
	}
	public void popularConcentracao(Long concentracaoParam){
		if(concentracaoParam <= 7.25){
			concentracao = DominioEscoreConcentracao.E3;
		}else if(concentracaoParam > 7.25){
			concentracao = DominioEscoreConcentracao.E0;
		}
	}
	public void popularLeucocitos(Long LeucocitosParam){
		if(LeucocitosParam < 15){
			leucocitos = DominioEscoreLeucocitos.E0;
		}else if(LeucocitosParam >= 15){
			leucocitos = DominioEscoreLeucocitos.E2;
		}
	}
	public void popularBilirrubina(Long bilirrubinaParam){
		if(bilirrubinaParam == Long.valueOf("0")){
			bilirrubina = DominioEscoreBilirrubina.E0;
		}else if(bilirrubinaParam == Long.valueOf("4")){
			bilirrubina = DominioEscoreBilirrubina.E4;
		}else if(bilirrubinaParam == Long.valueOf("5")){
			bilirrubina = DominioEscoreBilirrubina.E5;
		}
	}
	public void popularPlaquetas(Long plaquetasParam){
		if(plaquetasParam < 20){
			plaquetas = DominioEscorePlaquetas.E0;
		}else if(plaquetasParam >= 20  && plaquetasParam < 50){
			plaquetas = DominioEscorePlaquetas.E8;
		}else if(plaquetasParam >=50  && plaquetasParam < 100){
			plaquetas = DominioEscorePlaquetas.E5;
		}else if(plaquetasParam >= 100){
			plaquetas = DominioEscorePlaquetas.E0;
		}
	}
	public void popularCreatinina(Long creatininaParam){
		if(creatininaParam == Long.valueOf("0")){
			creatinina = DominioEscoreCreatinina.E0;
		}else if(creatininaParam == Long.valueOf("2")){
			creatinina = DominioEscoreCreatinina.E2;
		}else if(creatininaParam == Long.valueOf("7")){
			creatinina = DominioEscoreCreatinina.E7;
		}else if(creatininaParam == Long.valueOf("8")){
			creatinina = DominioEscoreCreatinina.E8;
		}
	}
	public void populaIdade(){
		if(paciente.getIdade() == null){
			idade = DominioEscoreIdade.E0;
		}else{
			if(paciente.getIdade() < 40){
				idade = DominioEscoreIdade.E0;
			}else if(paciente.getIdade() >=40 && paciente.getIdade() < 60){
				idade = DominioEscoreIdade.E5;
			}else if(paciente.getIdade() >=60  && paciente.getIdade() < 70){
				idade = DominioEscoreIdade.E9;
			}else if(paciente.getIdade() >=70  && paciente.getIdade() < 75){
				idade = DominioEscoreIdade.E13;
			}else if(paciente.getIdade() >=75  && paciente.getIdade() < 80){
				idade = DominioEscoreIdade.E15;
			}else if(paciente.getIdade() >=80){
				idade = DominioEscoreIdade.E18;
			}
		}
	}
	public void populaPermanencia(){
		Integer dias = 0 ;
		if(atendimento != null){
			if(atendimento.getDthrFim() != null){
				dias = DateUtil.calcularDiasEntreDatas(atendimento.getDthrInicio(), atendimento.getDthrFim());
			}else{
				dias = DateUtil.calcularDiasEntreDatas(atendimento.getDthrInicio(), new Date());
			}if(dias < 14){
				permanencia = DominioEscorePermanencia.E0;
			}else if(dias >=14 && dias < 28){
				permanencia = DominioEscorePermanencia.E6;
			}else if(dias >=28){
				permanencia = DominioEscorePermanencia.E7;
			}
		}
	}
	public String gravar(){
		saps3.setIdade(Short.valueOf(idade.getNumero()));
		saps3.setPermanencia(Short.valueOf(permanencia.getNumero()));
		saps3.setProcedencia(Short.valueOf(localizacao.getNumero()));
		if(DominioSimNao.S.equals(tratCancer)){
			saps3.setComorbidadeTratCancer(tratCancerValor);
		}else{
			saps3.setComorbidadeTratCancer((short)0);
		}
		if(DominioSimNao.S.equals(cancerHemo)){
			saps3.setComorbidadeCancerHemato(cancerHemoValor);
		}else{
			saps3.setComorbidadeCancerHemato((short)0);
		}
		if(DominioSimNao.S.equals(cirrose)){
			saps3.setComorbidadeCirrose(cirroseValor);
		}else{
			saps3.setComorbidadeCirrose((short)0);
		}
		if(DominioSimNao.S.equals(cancer)){
			saps3.setComorbidadeCancer(cancerValor);
		}else{
			saps3.setComorbidadeCancer((short)0);
		}
		if(DominioSimNao.S.equals(icc)){
			saps3.setComorbidadeIcc(iccValor);
		}else{
			saps3.setComorbidadeIcc((short)0);
		}
		if(DominioSimNao.S.equals(aids)){
			saps3.setComorbidadeAids(aidsValor);
		}else{
			saps3.setComorbidadeAids((short)0);
		}
		if(DominioSimNao.S.equals(terap)){
			saps3.setDrogasVasoativas(terapValor);
		}else{
			saps3.setDrogasVasoativas((short)0);
		}
		saps3.setTipoAdmissao(Short.valueOf(tipoAdmissaoUti.getNumero()));
		saps3.setRazoesCardiovascular(Short.valueOf(cardiovascular.getNumero()));
		saps3.setRazoesDigestivo(Short.valueOf(digestivo.getNumero()));
		saps3.setRazoesHepatico(Short.valueOf(hepatico.getNumero()));
		saps3.setRazoesNeurologico(Short.valueOf(neurologico.getNumero()));
		saps3.setEstadoCirurgicoAdmissao(Short.valueOf(estadoCirurgico.getNumero()));
		saps3.setTiposCirurgia(Short.valueOf(tipoCirurgia.getNumero()));
		if(DominioSimNao.S.equals(nasocomial)){
			saps3.setInfeccaoNosocomial(nasocomialValor);
		}else{
			saps3.setInfeccaoNosocomial((short)0);
		}
		if(DominioSimNao.S.equals(respiratoria)){
			saps3.setInfeccaoRespiratoria(respiratoriaValor);
		}else{
			saps3.setInfeccaoRespiratoria((short)0);
		}
		saps3.setEscalaGlaslow(Short.valueOf(glasglow.getNumero()));
		saps3.setBilirrubinaTotal(Short.valueOf(bilirrubina.getNumero()));
		saps3.setTemperaturaCorporal(Short.valueOf(temperatura.getNumero()));
		saps3.setCreatinina(Short.valueOf(creatinina.getNumero()));
		saps3.setFreqCardiaca(Short.valueOf(frequencia.getNumero()));
		saps3.setLeucocitos(Short.valueOf(leucocitos.getNumero()));
		saps3.setConcentracaoPh(Short.valueOf(concentracao.getNumero()));
		saps3.setPlaquetas(Short.valueOf(plaquetas.getNumero()));
		saps3.setPresssaoSistolica(Short.valueOf(pressao.getNumero()));
		saps3.setOxigenacao(Short.valueOf(oxigenacao.getNumero()));
		saps3.setIndSituacao(DominioSituacaoSaps3.E);
		prescricaoMedicaFacade.persistir(saps3);
		this.apresentarMsgNegocio(Severity.INFO, "MPM_ESCORE_SAPS3_SALVO_SUCESSO");
		return verificarPrescricaoMedicaController.criarPrescricao();
	}
	public void calcular(){
		pontosSaps = null; obito = null; obitoCustom = null;
		if(validaForm()){
			calcularPontosSaps3(); calcularObito(); calcularObitoCustom();
		}
	}
	public void calcularObito(){
		obito = prescricaoMedicaFacade.calcularObito(pontosSaps, somaObito, multiObito, subObito);
	}
	public void calcularObitoCustom(){
		obitoCustom = prescricaoMedicaFacade.calcularObito(pontosSaps, somaObitoCustom, multiObitoCustom, subObitoCustom);
	}
	public Boolean isNotNull(Object item,Boolean ret){
		if(item != null){
			if(ret){ return true; }
		}
		return false;
	}
	public Boolean validaForm(){
		Boolean retorno = true;
		retorno = isNotNull(idade,retorno);
		retorno = isNotNull(permanencia,retorno);
		retorno = isNotNull(localizacao,retorno);
		retorno = isNotNull(tratCancer,retorno);
		retorno = isNotNull(cancerHemo,retorno);
		retorno = isNotNull(cirrose,retorno);
		retorno = isNotNull(cancer,retorno);
		retorno = isNotNull(icc,retorno);
		retorno = isNotNull(aids,retorno);
		retorno = isNotNull(terap,retorno);
		retorno = isNotNull(tipoAdmissaoUti,retorno);
		retorno = isNotNull(cardiovascular,retorno);
		retorno = isNotNull(digestivo,retorno);
		retorno = isNotNull(hepatico,retorno);
		retorno = isNotNull(neurologico,retorno);
		retorno = isNotNull(estadoCirurgico,retorno);
		retorno = isNotNull(tipoCirurgia,retorno);
		retorno = isNotNull(nasocomial,retorno);
		retorno = isNotNull(respiratoria,retorno);
		retorno = isNotNull(glasglow,retorno);
		retorno = isNotNull(bilirrubina,retorno);
		retorno = isNotNull(temperatura,retorno);
		retorno = isNotNull(creatinina,retorno);
		retorno = isNotNull(frequencia,retorno);
		retorno = isNotNull(leucocitos,retorno);
		retorno = isNotNull(concentracao,retorno);
		retorno = isNotNull(plaquetas,retorno);
		retorno = isNotNull(pressao,retorno);
		retorno = isNotNull(oxigenacao,retorno);
		return retorno;
	}
	public void calcularPontosSaps3(){
		pontosSaps = (short)0;
		pontosSaps = (short) (pontosSaps + Short.valueOf(idade.getNumero()));
		pontosSaps = (short) (pontosSaps + Short.valueOf(permanencia.getNumero()));
		pontosSaps = (short) (pontosSaps + Short.valueOf(localizacao.getNumero()));
		if(DominioSimNao.S.equals(tratCancer)){
			pontosSaps = (short) (pontosSaps + Short.valueOf(tratCancerValor));
		}
		if(DominioSimNao.S.equals(cancerHemo)){
			pontosSaps = (short) (pontosSaps + Short.valueOf(cancerHemoValor));
		}
		if(DominioSimNao.S.equals(cirrose)){
			pontosSaps = (short) (pontosSaps + Short.valueOf(cirroseValor));
		}
		if(DominioSimNao.S.equals(cancer)){
			pontosSaps = (short) (pontosSaps + Short.valueOf(cancerValor));
		}
		if(DominioSimNao.S.equals(icc)){
			pontosSaps = (short) (pontosSaps + Short.valueOf(iccValor));
		}
		if(DominioSimNao.S.equals(aids)){
			pontosSaps = (short) (pontosSaps + Short.valueOf(aidsValor));
		}
		if(DominioSimNao.S.equals(terap)){
			pontosSaps = (short) (pontosSaps + Short.valueOf(terapValor));
		}	
		pontosSaps = (short) (pontosSaps + Short.valueOf(tipoAdmissaoUti.getNumero()));
		pontosSaps = (short) (pontosSaps + Short.valueOf(cardiovascular.getNumero()));
		pontosSaps = (short) (pontosSaps + Short.valueOf(digestivo.getNumero()));
		pontosSaps = (short) (pontosSaps + Short.valueOf(hepatico.getNumero()));
		pontosSaps = (short) (pontosSaps + Short.valueOf(neurologico.getNumero()));
		pontosSaps = (short) (pontosSaps + Short.valueOf(estadoCirurgico.getNumero()));
		pontosSaps = (short) (pontosSaps + Short.valueOf(tipoCirurgia.getNumero()));
		if(DominioSimNao.S.equals(nasocomial)){
			pontosSaps = (short) (pontosSaps + Short.valueOf(nasocomialValor));
		}
		if(DominioSimNao.S.equals(respiratoria)){
			pontosSaps = (short) (pontosSaps + Short.valueOf(respiratoriaValor));
		}
		pontosSaps = (short) (pontosSaps + Short.valueOf(glasglow.getNumero()));
		pontosSaps = (short) (pontosSaps + Short.valueOf(bilirrubina.getNumero()));
		pontosSaps = (short) (pontosSaps + Short.valueOf(temperatura.getNumero()));
		pontosSaps = (short) (pontosSaps + Short.valueOf(creatinina.getNumero()));
		pontosSaps = (short) (pontosSaps + Short.valueOf(frequencia.getNumero()));
		pontosSaps = (short) (pontosSaps + Short.valueOf(leucocitos.getNumero()));
		pontosSaps = (short) (pontosSaps + Short.valueOf(concentracao.getNumero()));
		pontosSaps = (short) (pontosSaps + Short.valueOf(plaquetas.getNumero()));
		pontosSaps = (short) (pontosSaps + Short.valueOf(pressao.getNumero()));
		pontosSaps = (short) (pontosSaps + Short.valueOf(oxigenacao.getNumero()));	
	}
	public void onTabChange(TabChangeEvent event) {
		 String abaSelecionada = event.getTab().getId();
		 if(StringUtils.isNotBlank(abaSelecionada)) {
		 Integer indiceAbaSelecionada = Integer.valueOf(StringUtils.replace(abaSelecionada, "tab", ""));
		 activeTab = indiceAbaSelecionada;
		 }
	}
	public void tab1(){
		activeTab = 0;
	}
	public void tab2(){
		activeTab = 1;
	}
	public void tab3(){
		activeTab = 2;
	}
	public void setarCamposAba1(){
		setarCombosPrimeiraParte();
		if(saps3.getComorbidadeCancer() != null){
			if(saps3.getComorbidadeCancer() == Short.valueOf("0")){
				cancer = DominioSimNao.N;
			}else{
				cancer = DominioSimNao.S;
			}
		}
		if(saps3.getComorbidadeIcc() != null){
			if(saps3.getComorbidadeIcc() == Short.valueOf("0")){
				icc = DominioSimNao.N;
			}else{
				icc = DominioSimNao.S;
			}
		}
		if(saps3.getComorbidadeAids() != null){
			if(saps3.getComorbidadeAids() == Short.valueOf("0")){
				aids = DominioSimNao.N;
			}else{
				aids = DominioSimNao.S;
			}
		}
		if(saps3.getDrogasVasoativas() != null){
			if(saps3.getDrogasVasoativas() == Short.valueOf("0")){
				terap = DominioSimNao.N;
			}else{
				terap = DominioSimNao.S;
			}
		}
	}
	public void setarCamposAba2(){
		setaCardiovascular();
		setaDigestivo();
		setaCombosFinais();
		setaCombosBooleanas();
	}
	public void setarCamposAba3(){
		if(saps3.getEscalaGlaslow() == Short.valueOf("0")){
			glasglow = DominioEscoreGlasglow.E0;
		}else if (saps3.getEscalaGlaslow() == Short.valueOf("10")){
			glasglow = DominioEscoreGlasglow.E10;
		}else if (saps3.getEscalaGlaslow() == Short.valueOf("15")){
			glasglow = DominioEscoreGlasglow.E15;
		}else if (saps3.getEscalaGlaslow() == Short.valueOf("2")){
			glasglow = DominioEscoreGlasglow.E2;
		}else if (saps3.getEscalaGlaslow() == Short.valueOf("7")){
			glasglow = DominioEscoreGlasglow.E7;
		}
		if(saps3.getTemperaturaCorporal() == Short.valueOf("0")){
			temperatura = DominioEscoreTemperatura.E0;
		}else if (saps3.getTemperaturaCorporal() == Short.valueOf("7")){
			temperatura = DominioEscoreTemperatura.E7;
		}
		if(saps3.getPresssaoSistolica() == Short.valueOf("0")){
			pressao = DominioEscorePressao.E0;
		}else if (saps3.getPresssaoSistolica() == Short.valueOf("11")){
			pressao = DominioEscorePressao.E11;
		}else if (saps3.getPresssaoSistolica() == Short.valueOf("3")){
			pressao = DominioEscorePressao.E3;
		}else if (saps3.getPresssaoSistolica() == Short.valueOf("8")){
			pressao = DominioEscorePressao.E8;
		}
		if(saps3.getOxigenacao() == Short.valueOf("0")){
			oxigenacao = DominioEscoreOxigenacao.E0;
		}else if (saps3.getOxigenacao() == Short.valueOf("11")){
			oxigenacao = DominioEscoreOxigenacao.E11;
		}else if (saps3.getOxigenacao() == Short.valueOf("5")){
			oxigenacao = DominioEscoreOxigenacao.E5;
		}else if (saps3.getOxigenacao() == Short.valueOf("7")){
			oxigenacao = DominioEscoreOxigenacao.E7;
		}
	}
	public void setarCombosPrimeiraParte(){
		if(saps3.getProcedencia() != null){
			if(saps3.getProcedencia() == Short.valueOf("0")){
				localizacao = DominioEscoreLocalizacao.E0;
			}else if(saps3.getProcedencia() == Short.valueOf("5")){
				localizacao = DominioEscoreLocalizacao.E5;
			}else if(saps3.getProcedencia() == Short.valueOf("7")){
				localizacao = DominioEscoreLocalizacao.E7;
			}else if(saps3.getProcedencia() == Short.valueOf("8")){
				localizacao = DominioEscoreLocalizacao.E8;
			}
		}
		if(saps3.getComorbidadeTratCancer() != null){
			if(saps3.getComorbidadeTratCancer() == Short.valueOf("0")){
				tratCancer = DominioSimNao.N;
			}else{
				tratCancer = DominioSimNao.S;
			}
		}
		if(saps3.getComorbidadeCancerHemato() != null){
			if(saps3.getComorbidadeCancerHemato() == Short.valueOf("0")){
				cancerHemo = DominioSimNao.N;
			}else{
				cancerHemo = DominioSimNao.S;
			}
		}
		if(saps3.getComorbidadeCirrose() != null){
			if(saps3.getComorbidadeCirrose() == Short.valueOf("0")){
				cirrose = DominioSimNao.N;
			}else{
				cirrose = DominioSimNao.S;
			}
		}
	}
	public void setaCardiovascular(){
		if(saps3.getRazoesCardiovascular() != null){
			if(saps3.getRazoesCardiovascular() == Short.valueOf("0")){
				cardiovascular = DominioEscoreCardiovascular.E0;
			}else if(saps3.getRazoesCardiovascular() == Short.valueOf("3")){
				cardiovascular = DominioEscoreCardiovascular.E3;
			}else if(saps3.getRazoesCardiovascular() == Short.valueOf("5")){
				cardiovascular = DominioEscoreCardiovascular.E5;
			}else if(saps3.getRazoesCardiovascular() == Short.valueOf("-5")){
				cardiovascular = DominioEscoreCardiovascular.E_5;
			}
		}
	}
	public void setaDigestivo(){
		if(saps3.getRazoesDigestivo() != null){
			if(saps3.getRazoesDigestivo() == Short.valueOf("0")){
				digestivo = DominioEscoreDigestivo.E0;
			}else if(saps3.getRazoesDigestivo() == Short.valueOf("3")){
				digestivo = DominioEscoreDigestivo.E3;
			}else if(saps3.getRazoesDigestivo() == Short.valueOf("9")){
				digestivo = DominioEscoreDigestivo.E9;
			}
		}
	}
	public void setaCombosFinais(){
		if(saps3.getRazoesNeurologico() != null){
			if(saps3.getRazoesNeurologico() == Short.valueOf("0")){
				neurologico = DominioEscoreNeurologico.E0;
			}else if(saps3.getRazoesNeurologico() == Short.valueOf("11")){
				neurologico = DominioEscoreNeurologico.E11;
			}else if(saps3.getRazoesNeurologico() == Short.valueOf("4")){
				neurologico = DominioEscoreNeurologico.E4;
			}else if(saps3.getRazoesNeurologico() == Short.valueOf("7")){
				neurologico = DominioEscoreNeurologico.E7;
			}else if(saps3.getRazoesNeurologico() == Short.valueOf("-4")){
				neurologico = DominioEscoreNeurologico.E_4;
			}
		}
		if(saps3.getEstadoCirurgicoAdmissao() != null){
			if(saps3.getEstadoCirurgicoAdmissao() == Short.valueOf("0")){
				estadoCirurgico = DominioEscoreEstadoCirurgico.E0;
			}else if(saps3.getEstadoCirurgicoAdmissao() == Short.valueOf("5")){
				estadoCirurgico = DominioEscoreEstadoCirurgico.E5;
			}else if(saps3.getEstadoCirurgicoAdmissao() == Short.valueOf("6")){
				estadoCirurgico = DominioEscoreEstadoCirurgico.E6;
			}
		}
		if(saps3.getTiposCirurgia() != null){
			if(saps3.getTiposCirurgia() == Short.valueOf("0")){
				tipoCirurgia = DominioEscoreTipoCirurgia.E0;
			}else if(saps3.getTiposCirurgia() == Short.valueOf("5")){
				tipoCirurgia = DominioEscoreTipoCirurgia.E5;
			}else if(saps3.getTiposCirurgia() == Short.valueOf("6")){
				tipoCirurgia = DominioEscoreTipoCirurgia.E6;
			}else if(saps3.getTiposCirurgia() == Short.valueOf("11")){
				tipoCirurgia = DominioEscoreTipoCirurgia.E11;
			}else if(saps3.getTiposCirurgia() == Short.valueOf("8")){
				tipoCirurgia = DominioEscoreTipoCirurgia.E8;
			}
		}
	}
	public void setaCombosBooleanas(){
		if(saps3.getTipoAdmissao() != null){
			if(saps3.getTipoAdmissao() == Short.valueOf("0")){
				tipoAdmissaoUti = DominioEscorePlanejado.E0;
			}else{
				tipoAdmissaoUti = DominioEscorePlanejado.E3;
			}
		}
		if(saps3.getRazoesHepatico() != null){
			if(saps3.getRazoesHepatico() == Short.valueOf("0")){
				hepatico = DominioEscoreHepatico.E0;
			}else if(saps3.getRazoesHepatico() == Short.valueOf("6")){
				hepatico = DominioEscoreHepatico.E6;
			}
		}
		if(saps3.getInfeccaoNosocomial() != null){
			if(saps3.getInfeccaoNosocomial() == Short.valueOf("0")){
				nasocomial = DominioSimNao.N;
			}else{
				nasocomial = DominioSimNao.S;
			}
		}
		if(saps3.getInfeccaoRespiratoria() != null){
			if(saps3.getInfeccaoRespiratoria() == Short.valueOf("0")){
				respiratoria = DominioSimNao.N;}else{
				respiratoria = DominioSimNao.S;}
		}
	}
	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}
	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public AipPacientes getPaciente() {
		return paciente;
	}
	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public Integer getActiveTab() {
		return activeTab;
	}
	public void setActiveTab(Integer activeTab) {
		this.activeTab = activeTab;
	}
	public String getTitleAba1() {
		return titleAba1;
	}
	public void setTitleAba1(String titleAba1) {
		this.titleAba1 = titleAba1;
	}
	public String getTitleAba2() {
		return titleAba2;
	}
	public void setTitleAba2(String titleAba2) {
		this.titleAba2 = titleAba2;
	}
	public String getTitleAba3() {
		return titleAba3;
	}
	public void setTitleAba3(String titleAba3) {
		this.titleAba3 = titleAba3;
	}
	public DominioEscoreIdade getIdade() {
		return idade;
	}
	public void setIdade(DominioEscoreIdade idade) {
		this.idade = idade;
	}
	public DominioEscorePermanencia getPermanencia() {
		return permanencia;
	}
	public void setPermanencia(DominioEscorePermanencia permanencia) {
		this.permanencia = permanencia;
	}
	public DominioEscoreLocalizacao getLocalizacao() {
		return localizacao;
	}
	public void setLocalizacao(DominioEscoreLocalizacao localizacao) {
		this.localizacao = localizacao;
	}
	public DominioSimNao getTratCancer() {
		return tratCancer;
	}
	public void setTratCancer(DominioSimNao tratCancer) {
		this.tratCancer = tratCancer;
	}
	public DominioSimNao getCancerHemo() {
		return cancerHemo;
	}
	public void setCancerHemo(DominioSimNao cancerHemo) {
		this.cancerHemo = cancerHemo;
	}
	public DominioSimNao getCirrose() {
		return cirrose;
	}
	public void setCirrose(DominioSimNao cirrose) {
		this.cirrose = cirrose;
	}
	public DominioSimNao getCancer() {
		return cancer;
	}
	public void setCancer(DominioSimNao cancer) {
		this.cancer = cancer;
	}
	public DominioSimNao getIcc() {
		return icc;
	}
	public void setIcc(DominioSimNao icc) {
		this.icc = icc;
	}
	public DominioSimNao getAids() {
		return aids;
	}
	public void setAids(DominioSimNao aids) {
		this.aids = aids;
	}
	public DominioSimNao getTerap() {
		return terap;
	}
	public void setTerap(DominioSimNao terap) {
		this.terap = terap;
	}
	public DominioEscorePlanejado getTipoAdmissaoUti() {
		return tipoAdmissaoUti;
	}
	public void setTipoAdmissaoUti(DominioEscorePlanejado tipoAdmissaoUti) {
		this.tipoAdmissaoUti = tipoAdmissaoUti;
	}
	public DominioEscoreCardiovascular getCardiovascular() {
		return cardiovascular;
	}
	public void setCardiovascular(DominioEscoreCardiovascular cardiovascular) {
		this.cardiovascular = cardiovascular;
	}
	public DominioEscoreDigestivo getDigestivo() {
		return digestivo;
	}
	public void setDigestivo(DominioEscoreDigestivo digestivo) {
		this.digestivo = digestivo;
	}
	public DominioEscoreHepatico getHepatico() {
		return hepatico;
	}
	public void setHepatico(DominioEscoreHepatico hepatico) {
		this.hepatico = hepatico;
	}
	public DominioEscoreNeurologico getNeurologico() {
		return neurologico;
	}
	public void setNeurologico(DominioEscoreNeurologico neurologico) {
		this.neurologico = neurologico;
	}
	public DominioEscoreEstadoCirurgico getEstadoCirurgico() {
		return estadoCirurgico;
	}
	public void setEstadoCirurgico(DominioEscoreEstadoCirurgico estadoCirurgico) {
		this.estadoCirurgico = estadoCirurgico;
	}
	public DominioEscoreTipoCirurgia getTipoCirurgia() {
		return tipoCirurgia;
	}
	public void setTipoCirurgia(DominioEscoreTipoCirurgia tipoCirurgia) {
		this.tipoCirurgia = tipoCirurgia;
	}
	public DominioSimNao getNasocomial() {
		return nasocomial;
	}
	public void setNasocomial(DominioSimNao nasocomial) {
		this.nasocomial = nasocomial;
	}
	public DominioSimNao getRespiratoria() {
		return respiratoria;
	}
	public void setRespiratoria(DominioSimNao respiratoria) {
		this.respiratoria = respiratoria;
	}
	public AghAtendimentos getAtendimento() {
		return atendimento;
	}
	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}
	public MpmEscoreSaps3 getSaps3() {
		return saps3;
	}
	public void setSaps3(MpmEscoreSaps3 saps3) {
		this.saps3 = saps3;
	}
	public Short getTratCancerValor() {
		return tratCancerValor;
	}
	public void setTratCancerValor(Short tratCancerValor) {
		this.tratCancerValor = tratCancerValor;
	}
	public Short getCancerHemoValor() {
		return cancerHemoValor;
	}
	public void setCancerHemoValor(Short cancerHemoValor) {
		this.cancerHemoValor = cancerHemoValor;
	}
	public Short getCirroseValor() {
		return cirroseValor;
	}
	public void setCirroseValor(Short cirroseValor) {
		this.cirroseValor = cirroseValor;
	}
	public Short getCancerValor() {
		return cancerValor;
	}
	public void setCancerValor(Short cancerValor) {
		this.cancerValor = cancerValor;
	}
	public Short getIccValor() {
		return iccValor;
	}
	public void setIccValor(Short iccValor) {
		this.iccValor = iccValor;
	}
	public Short getAidsValor() {
		return aidsValor;
	}
	public void setAidsValor(Short aidsValor) {
		this.aidsValor = aidsValor;
	}
	public Short getTerapValor() {
		return terapValor;
	}
	public void setTerapValor(Short terapValor) {
		this.terapValor = terapValor;
	}
	public DominioEscoreGlasglow getGlasglow() {
		return glasglow;
	}
	public void setGlasglow(DominioEscoreGlasglow glasglow) {
		this.glasglow = glasglow;
	}
	public DominioEscoreTemperatura getTemperatura() {
		return temperatura;
	}
	public void setTemperatura(DominioEscoreTemperatura temperatura) {
		this.temperatura = temperatura;
	}
	public DominioEscoreCreatinina getCreatinina() {
		return creatinina;
	}
	public void setCreatinina(DominioEscoreCreatinina creatinina) {
		this.creatinina = creatinina;
	}
	public DominioEscoreFrequencia getFrequencia() {
		return frequencia;
	}
	public void setFrequencia(DominioEscoreFrequencia frequencia) {
		this.frequencia = frequencia;
	}
	public DominioEscoreLeucocitos getLeucocitos() {
		return leucocitos;
	}
	public void setLeucocitos(DominioEscoreLeucocitos leucocitos) {
		this.leucocitos = leucocitos;
	}
	public DominioEscoreConcentracao getConcentracao() {
		return concentracao;
	}
	public void setConcentracao(DominioEscoreConcentracao concentracao) {
		this.concentracao = concentracao;
	}
	public DominioEscorePlaquetas getPlaquetas() {
		return plaquetas;
	}
	public void setPlaquetas(DominioEscorePlaquetas plaquetas) {
		this.plaquetas = plaquetas;
	}
	public DominioEscorePressao getPressao() {
		return pressao;
	}
	public void setPressao(DominioEscorePressao pressao) {
		this.pressao = pressao;
	}
	public DominioEscoreOxigenacao getOxigenacao() {
		return oxigenacao;
	}
	public void setOxigenacao(DominioEscoreOxigenacao oxigenacao) {
		this.oxigenacao = oxigenacao;
	}
	public Short getNasocomialValor() {
		return nasocomialValor;
	}
	public void setNasocomialValor(Short nasocomialValor) {
		this.nasocomialValor = nasocomialValor;
	}
	public Short getRespiratoriaValor() {
		return respiratoriaValor;
	}
	public void setRespiratoriaValor(Short respiratoriaValor) {
		this.respiratoriaValor = respiratoriaValor;
	}
	public DominioEscoreBilirrubina getBilirrubina() {
		return bilirrubina;
	}
	public void setBilirrubina(DominioEscoreBilirrubina bilirrubina) {this.bilirrubina = bilirrubina;}
	public Short getPontosSaps() { 	return pontosSaps; }
	public void setPontosSaps(Short pontosSaps) { this.pontosSaps = pontosSaps; }
	public Double getSomaObito() { return somaObito; }
	public void setSomaObito(Double somaObito) { this.somaObito = somaObito; }
	public Double getMultiObito() { return multiObito; }
	public void setMultiObito(Double multiObito) { this.multiObito = multiObito; }
	public Double getSubObito() { return subObito; }
	public void setSubObito(Double subObito) { this.subObito = subObito; }
	public Double getSomaObitoCustom() { return somaObitoCustom; }
	public void setSomaObitoCustom(Double somaObitoCustom) { this.somaObitoCustom = somaObitoCustom; }
	public Double getMultiObitoCustom() { return multiObitoCustom; }
	public void setMultiObitoCustom(Double multiObitoCustom) { this.multiObitoCustom = multiObitoCustom; }
	public Double getSubObitoCustom() { return subObitoCustom; }
	public void setSubObitoCustom(Double subObitoCustom) { this.subObitoCustom = subObitoCustom; }
	public Double getObito() { return obito; }
	public void setObito(Double obito) { this.obito = obito; }
	public Double getObitoCustom()  {return obitoCustom; }
	public void setObitoCustom(Double obitoCustom) {this.obitoCustom = obitoCustom;}
	public DominioEscoreIdade[] listarIdades(){ return DominioEscoreIdade.values(); }
	public DominioEscorePermanencia[] listarPermanencias(){ return DominioEscorePermanencia.values(); }
	public DominioEscoreLocalizacao[] listarLocalizacoes(){ return DominioEscoreLocalizacao.values(); }
	public DominioEscorePlanejado[] listarPlanejado(){ return DominioEscorePlanejado.values(); }
	public DominioSimNao[] listarSimNao(){ return DominioSimNao.values(); }
	public DominioEscoreCardiovascular[] listarCardiovascular(){ return DominioEscoreCardiovascular.values(); }
	public DominioEscoreDigestivo[] listarDigestivo(){  return DominioEscoreDigestivo.values(); }
	public DominioEscoreHepatico[] listarHepatico(){  return DominioEscoreHepatico.values(); }
	public DominioEscoreNeurologico[] listarNeurologico(){ return DominioEscoreNeurologico.values(); }
	public DominioEscoreEstadoCirurgico[] listarEstadoCirurgico(){ return DominioEscoreEstadoCirurgico.values(); }
	public DominioEscoreTipoCirurgia[] listarTipoCirurgia(){ return DominioEscoreTipoCirurgia.values(); }
	public DominioEscoreGlasglow[] listarGlasglow(){ return DominioEscoreGlasglow.values(); }
	public DominioEscoreLeucocitos[] listarLeucocitos(){ return DominioEscoreLeucocitos.values(); }
	public DominioEscoreBilirrubina[] listarBilirrubina(){ return DominioEscoreBilirrubina.values(); }
	public DominioEscoreConcentracao[] listarConcentracao(){ return DominioEscoreConcentracao.values(); }
	public DominioEscoreTemperatura[] listarTemperatura(){ return DominioEscoreTemperatura.values(); }
	public DominioEscorePlaquetas[] listarPlaquetas(){ return DominioEscorePlaquetas.values(); }
	public DominioEscoreCreatinina[] listarCreatinina(){ return DominioEscoreCreatinina.values(); }
	public DominioEscorePressao[] listarPressao(){ return DominioEscorePressao.values(); }
	public DominioEscoreFrequencia[] listarFrequencia(){ return DominioEscoreFrequencia.values(); }
	public DominioEscoreOxigenacao[] listarOxigenacao(){ return DominioEscoreOxigenacao.values(); }
}
