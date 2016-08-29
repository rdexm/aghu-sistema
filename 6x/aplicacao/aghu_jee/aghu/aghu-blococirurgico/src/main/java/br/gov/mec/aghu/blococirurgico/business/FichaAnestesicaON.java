package br.gov.mec.aghu.blococirurgico.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaAnestesiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaEquipeAnestesiaDAO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaVO;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Aplicacao;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaEquipeAnestesia;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class FichaAnestesicaON extends BaseBusiness {

    private static final String MENSAGEM_ATO_ANESTESICO_A = "MENSAGEM_ATO_ANESTESICO_A";
    private static final String MENSAGEM_ATO_ANESTESICO_B = "MENSAGEM_ATO_ANESTESICO_B";
    private static final String MENSAGEM_ATO_ANESTESICO_C = "MENSAGEM_ATO_ANESTESICO_C";
    private static final String MENSAGEM_ATO_ANESTESICO_D = "MENSAGEM_ATO_ANESTESICO_D";
    private static final String MENSAGEM_ATO_ANESTESICO_E = "MENSAGEM_ATO_ANESTESICO_E";
    private static final String MENSAGEM_ATO_ANESTESICO_F = "MENSAGEM_ATO_ANESTESICO_F";
    private static final String MENSAGEM_ATO_ANESTESICO_G = "MENSAGEM_ATO_ANESTESICO_G";
    private static final String MENSAGEM_ATO_ANESTESICO_H = "MENSAGEM_ATO_ANESTESICO_H";
    private static final String MENSAGEM_ATO_ANESTESICO_I = "MENSAGEM_ATO_ANESTESICO_I";
    private static final String MENSAGEM_ATO_ANESTESICO_J = "MENSAGEM_ATO_ANESTESICO_J";
    private static final String MENSAGEM_ATO_ANESTESICO_L = "MENSAGEM_ATO_ANESTESICO_L";
    private static final String MENSAGEM_ATO_ANESTESICO_M = "MENSAGEM_ATO_ANESTESICO_M";
    private static final String MENSAGEM_ATO_ANESTESICO_N = "MENSAGEM_ATO_ANESTESICO_N";

    @EJB
    private IServidorLogadoFacade servidorLogadoFacade;

    private static final Log LOG = LogFactory.getLog(FichaAnestesicaON.class);

    @Override
    @Deprecated
    protected Log getLogger() {
	return LOG;
    }

    @Inject
    private MbcFichaAnestesiasDAO mbcFichaAnestesiasDAO;

    @Inject
    private MbcCirurgiasDAO mbcCirurgiasDAO;

    @Inject
    private MbcFichaEquipeAnestesiaDAO mbcFichaEquipeAnestesiaDAO;

    @EJB
    private ICascaFacade iCascaFacade;

    @EJB
    private IPrescricaoMedicaFacade iPrescricaoMedicaFacade;

    @EJB
    private IRegistroColaboradorFacade iRegistroColaboradorFacade;

    @EJB
    private IParametroFacade iParametroFacade;

    private static final long serialVersionUID = -5858782477007373430L;

    /**
     * 
     * Procedure #24916 ORADB: P_HABILITA_BOTAO_ANESTESIA
     * 
     * @param crgSeq
     * @param dominioSituacaoCirurgia
     * @return Boolean(se podera ser habilitado ou não)
     * 
     */
    public Boolean habilitarBotaoAnestesia(Integer crgSeq, DominioSituacaoCirurgia dominioSituacaoCirurgia) throws BaseException {

	IParametroFacade parametroFacade = this.getParametroFacade();
	AghParametros parametrosAnestesia = new AghParametros();
	MbcCirurgias cirurgia = getMbcCirurgiasDAO().obterCirurgiaPorSeq(crgSeq);

	if (!getMbcFichaAnestesiasDAO().isOracle()) {
	    return Boolean.FALSE;
	}

	List<MbcFichaAnestesias> listaFichaAnestesias = getMbcFichaAnestesiasDAO().listarFichasAnestesiasPorCirurgias(crgSeq);

	// Não é permitido elaborar ficha de anestesia nova para cirurgias
	// canceladas
	// ou já realizadas (paciente já saiu da sala cirúrgica)
	if (DominioSituacaoCirurgia.CANC.equals(dominioSituacaoCirurgia) || DominioSituacaoCirurgia.RZDA.equals(dominioSituacaoCirurgia)) {

	    parametrosAnestesia = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_LIBERA_ANESTESIA_NOVA);

	    if ((DominioSimNao.N.toString()).equals(parametrosAnestesia.getVlrTexto()) && listaFichaAnestesias.isEmpty()) {
		return Boolean.FALSE;
	    }

	}

	Date dataLimite = null;

	// Caso exista ficha anestesica
	if (!listaFichaAnestesias.isEmpty()) {

	    DominioIndPendenteAmbulatorio pendente = listaFichaAnestesias.get(0).getPendente();

	    // Verifica se Anestesia esta pendente
	    if (pendente.getDescricao().equals(DominioIndPendenteAmbulatorio.P)
		    || pendente.getDescricao().equals(DominioIndPendenteAmbulatorio.R)) {

		parametrosAnestesia = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HRS_ANESTESIA_PENDENTE);
		dataLimite = calcularDataLimite(parametrosAnestesia.getCriadoEm(), parametrosAnestesia.getVlrNumerico());

		// Verifica se Anestesia esta concluida
	    } else if (pendente.getDescricao().equals(DominioIndPendenteAmbulatorio.V)) {

		parametrosAnestesia = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HRS_ANESTESIA_CONCLUIDA);
		dataLimite = calcularDataLimite(parametrosAnestesia.getCriadoEm(), parametrosAnestesia.getVlrNumerico());

		// Anestesia nova
	    } else {
		parametrosAnestesia = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HRS_ANESTESIA_NOVA);
		dataLimite = calcularDataLimite(dataLimite, parametrosAnestesia.getVlrNumerico());
	    }

	    // Não consta ficha para esta Anestesia - logo é uma Anestesia nova
	} else {

	    if (cirurgia.getDataInicioCirurgia() != null) {
		dataLimite = cirurgia.getDataInicioCirurgia();
	    } else {
		dataLimite = new Date();
	    }

	    parametrosAnestesia = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HRS_ANESTESIA_NOVA);
	    dataLimite = calcularDataLimite(dataLimite, parametrosAnestesia.getVlrNumerico());
	}

	// Habilita / Desabilita botão conforme a data limite calculada
	if (!(DateUtil.validaDataMaior(new Date(), dataLimite))) {
	    return Boolean.TRUE;
	}

	return Boolean.FALSE;
    }

    /**
     * 
     * Procedure #24916 ORADB: P_HABILITA_BOTAO_ANESTESIA
     * 
     * @param crgSeq
     * @param dominioSituacaoCirurgia
     * @return Boolean(se podera ser habilitado ou não)
     * 
     */
    public Boolean habilitarBotaoAnestesia(CirurgiaVO cirurgia) throws BaseException {

	IParametroFacade parametroFacade = this.getParametroFacade();
	AghParametros parametrosAnestesia = new AghParametros();

	if (!getMbcFichaAnestesiasDAO().isOracle()) {
	    return Boolean.FALSE;
	}

	// Não é permitido elaborar ficha de anestesia nova para cirurgias
	// canceladas
	// ou já realizadas (paciente já saiu da sala cirúrgica)
	if (DominioSituacaoCirurgia.CANC.equals(cirurgia.getSituacao()) || DominioSituacaoCirurgia.RZDA.equals(cirurgia.getSituacao())) {

	    parametrosAnestesia = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_LIBERA_ANESTESIA_NOVA);

	    if ((DominioSimNao.N.toString()).equals(parametrosAnestesia.getVlrTexto()) && cirurgia.getFichaPendente() != null) {
		return Boolean.FALSE;
	    }

	}

	Date dataLimite = null;

	// Caso exista ficha anestesica
	if (cirurgia.getFichaPendente() != null) {

	    DominioIndPendenteAmbulatorio pendente = cirurgia.getFichaPendente();

	    // Verifica se Anestesia esta pendente
	    if (pendente.getDescricao().equals(DominioIndPendenteAmbulatorio.P)
		    || pendente.getDescricao().equals(DominioIndPendenteAmbulatorio.R)) {

		parametrosAnestesia = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HRS_ANESTESIA_PENDENTE);
		dataLimite = calcularDataLimite(parametrosAnestesia.getCriadoEm(), parametrosAnestesia.getVlrNumerico());

		// Verifica se Anestesia esta concluida
	    } else if (pendente.getDescricao().equals(DominioIndPendenteAmbulatorio.V)) {

		parametrosAnestesia = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HRS_ANESTESIA_CONCLUIDA);
		dataLimite = calcularDataLimite(parametrosAnestesia.getCriadoEm(), parametrosAnestesia.getVlrNumerico());

		// Anestesia nova
	    } else {
		parametrosAnestesia = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HRS_ANESTESIA_NOVA);
		dataLimite = calcularDataLimite(dataLimite, parametrosAnestesia.getVlrNumerico());
	    }

	    // Não consta ficha para esta Anestesia - logo é uma Anestesia nova
	} else {

	    if (cirurgia.getDataInicioCirurgia() != null) {
		dataLimite = cirurgia.getDataInicioCirurgia();
	    } else {
		dataLimite = new Date();
	    }

	    parametrosAnestesia = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HRS_ANESTESIA_NOVA);
	    dataLimite = calcularDataLimite(dataLimite, parametrosAnestesia.getVlrNumerico());
	}

	// Habilita / Desabilita botão conforme a data limite calculada
	if (!(DateUtil.validaDataMaior(new Date(), dataLimite))) {
	    return Boolean.TRUE;
	}

	return Boolean.FALSE;
    }

    private Date calcularDataLimite(Date dataBase, BigDecimal parametroHoras) throws ApplicationBusinessException {

	if (parametroHoras == null || (parametroHoras.compareTo(BigDecimal.valueOf(1)) == -1)) {
	    return dataBase;
	} else {
	    BigDecimal limiteDeDias = parametroHoras.divide(BigDecimal.valueOf(24), 2, RoundingMode.HALF_DOWN);
	    dataBase = DateUtil.adicionaDiasFracao(dataBase, limiteDeDias.floatValue());
	}

	return dataBase;
    }

    public Boolean verificarExistenciaFichaAnestesica(Integer crgSeq) {

	List<MbcFichaAnestesias> listaFichaAnestesia = getMbcFichaAnestesiasDAO().listarFichasAnestesiasPorCirurgias(crgSeq);

	if (!listaFichaAnestesia.isEmpty()) {
	    return Boolean.TRUE;
	} else {
	    return Boolean.FALSE;
	}

    }

    /**
     * TODO Procedure
     * 
     * ORADB: MBCP_BOTAO_ANESTESIA (forms -> lógica para Anestesia: chamar ficha
     * anestesica)
     * 
     * @param telaVO
     * @param cirurgiaVO
     * @return Object Objeto retornado será ...
     * @throws ApplicationBusinessException
     */
    public String identificarAnestesia(Integer crgSeq, String microLogado, String nomePaciente, String procedimento) throws BaseException {

	StringBuilder mensagem = new StringBuilder();

	RapServidores servidor = servidorLogadoFacade.obterServidorLogado();

	List<MbcFichaAnestesias> listaFichaAnestesia = getMbcFichaAnestesiasDAO().listarFichasAnestesiasPorCirurgias(crgSeq);

	// Verifica se já existe ficha de anestesia para esta cirurgia
	if (!listaFichaAnestesia.isEmpty()) {

	    MbcFichaAnestesias fichaAnestesica = listaFichaAnestesia.get(0);

	    String nomeServidorEditado = getPrescricaoMedicaFacade().obtemNomeServidorEditado(
		    fichaAnestesica.getServidor().getId().getVinCodigo(), fichaAnestesica.getServidor().getId().getMatricula());

	    String nomeUsuarioResponsavel = nomeServidorEditado != null ? nomeServidorEditado : "";

	    Date dataLimite = fichaAnestesica.getData();
	    Date hoje = new Date();

	    // Não é possível elaborar Ficha de Anestesia para cirurgias em
	    // datas futuras
	    if (DateUtil.validaDataMaior(dataLimite, DateUtil.adicionaDias(hoje, 1))) {
		return mensagem.append(getResourceBundleValue(MENSAGEM_ATO_ANESTESICO_A)).toString();
	    }

	    // Usuario não faz parte da equipe
	    if (!verificarUsuarioFazParteEquipe(fichaAnestesica.getSeq(), servidor.getId().getMatricula(), servidor.getId().getVinCodigo())) {

		if (!microLogado.equalsIgnoreCase(fichaAnestesica.getMicrocomputador())) {
		    mensagem.append(getResourceBundleValue(MENSAGEM_ATO_ANESTESICO_B))
		    .append(getResourceBundleValue(MENSAGEM_ATO_ANESTESICO_C))
		    .append(nomeUsuarioResponsavel);
		    return mensagem.toString();
		}
	    }

	    switch (fichaAnestesica.getPendente()) {
	    case P:
		mensagem.append(getResourceBundleValue(MENSAGEM_ATO_ANESTESICO_D))
		.append(fichaAnestesica.getPaciente().getNome())
		.append(getResourceBundleValue(MENSAGEM_ATO_ANESTESICO_E));
		return mensagem.toString();

	    case R:
		mensagem.append(getResourceBundleValue(MENSAGEM_ATO_ANESTESICO_D))
		.append(fichaAnestesica.getPaciente().getNome())
		.append(getResourceBundleValue(MENSAGEM_ATO_ANESTESICO_F))
		.append(nomeUsuarioResponsavel)
		.append(getResourceBundleValue(MENSAGEM_ATO_ANESTESICO_G));
		return mensagem.toString();

	    case V:
		mensagem.append(getResourceBundleValue(MENSAGEM_ATO_ANESTESICO_D))
		.append(fichaAnestesica.getPaciente().getNome())
		.append(getResourceBundleValue(MENSAGEM_ATO_ANESTESICO_H))
		.append(nomeUsuarioResponsavel)
		.append(getResourceBundleValue(MENSAGEM_ATO_ANESTESICO_I));
		return mensagem.toString();

	    default:
		mensagem.append(getResourceBundleValue(MENSAGEM_ATO_ANESTESICO_D))
		.append(fichaAnestesica.getPaciente().getNome())
		.append(getResourceBundleValue(MENSAGEM_ATO_ANESTESICO_J));
		return mensagem.toString();
	    }

	} else {
	    mensagem.append(getResourceBundleValue(MENSAGEM_ATO_ANESTESICO_L))
	    .append(nomePaciente)
	    .append(getResourceBundleValue(MENSAGEM_ATO_ANESTESICO_M))
	    .append(procedimento)
	    .append(getResourceBundleValue(MENSAGEM_ATO_ANESTESICO_N));

	    String str = getPrescricaoMedicaFacade().obtemNomeServidorEditado(servidor.getId().getVinCodigo(),
		    servidor.getId().getMatricula());
	    mensagem.append(str != null ? str : "");

	    return mensagem.toString();
	}

    }

    public Boolean verificarUsuarioFazParteEquipe(Long seq, Integer matricula, Short vinculo) {

	List<MbcFichaEquipeAnestesia> listaFichaEquiepeAnestesia = getMbcFichaEquipeAnestesiaDAO()
		.pesquisarMbcFichaEquipeAnestesiasByFichaAnestesiaServidor(seq, matricula, vinculo);

	if (listaFichaEquiepeAnestesia.isEmpty()) {
	    return Boolean.FALSE;
	}

	return Boolean.TRUE;
    }

    public String obterUrlFichaAnestesica() throws BaseException {

	AghParametros parametrosAnestesia = new AghParametros();
	IParametroFacade parametroFacade = this.getParametroFacade();
	Aplicacao aplicacao = new Aplicacao();
	ICascaFacade cascaFacade = this.getCascaFacade();
	String caminho;

	parametrosAnestesia = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_NOME_APLICACAO_FICHA_ANESTESICA);

	aplicacao = cascaFacade.pesquisarAplicacaoPorNome(parametrosAnestesia.getVlrTexto()).get(0);

	caminho = aplicacao.getServidor() + ":" + aplicacao.getPorta() + "/" + aplicacao.getContexto() + "/" + "loginEncrypted.action";

	return caminho;
    }

    public Integer obterSeqFichaAnestesica(CirurgiaVO crgSelecionada) throws BaseException {

	if (this.habilitarBotaoAnestesia(crgSelecionada)) {

	    MbcFichaAnestesias fichaAnestesias = new MbcFichaAnestesias();
	    if (crgSelecionada.getFichaSeq() != null && crgSelecionada.getFichaSeq() > 0) {
		fichaAnestesias = getMbcFichaAnestesiasDAO().obterPorChavePrimaria(crgSelecionada.getFichaSeq());
	    }
	    return getMbcFichaAnestesiasDAO().obterFichaAnestesica(crgSelecionada, fichaAnestesias,
		    servidorLogadoFacade.obterServidorLogado());
	}

	return null;
    }

    // DAO
    protected MbcFichaAnestesiasDAO getMbcFichaAnestesiasDAO() {
	return mbcFichaAnestesiasDAO;
    }

    protected MbcFichaEquipeAnestesiaDAO getMbcFichaEquipeAnestesiaDAO() {
	return mbcFichaEquipeAnestesiaDAO;
    }

    protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
	return mbcCirurgiasDAO;
    }

    // FACADES
    protected IParametroFacade getParametroFacade() {
	return iParametroFacade;
    }

    protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
	return iPrescricaoMedicaFacade;
    }

    protected ICascaFacade getCascaFacade() {
	return iCascaFacade;
    }

    protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
	return iRegistroColaboradorFacade;
    }

}