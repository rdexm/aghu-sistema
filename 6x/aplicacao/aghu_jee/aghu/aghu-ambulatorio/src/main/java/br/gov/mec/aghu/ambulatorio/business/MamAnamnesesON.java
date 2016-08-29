package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.MamAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamExtratoRegistroDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRegistroDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoItemAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoItemEvolucaoDAO;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoRegistro;
import br.gov.mec.aghu.dominio.DominioTipoFormularioAlta;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamExtratoRegistro;
import br.gov.mec.aghu.model.MamExtratoRegistroId;
import br.gov.mec.aghu.model.MamRegistro;
import br.gov.mec.aghu.model.MamTipoItemAnamneses;
import br.gov.mec.aghu.model.MamTipoItemEvolucao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class MamAnamnesesON extends BaseBusiness {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private static final Log LOG = LogFactory.getLog(MamAnamnesesON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ICascaFacade cascaFacade;

    @EJB
    private IParametroFacade parametroFacade;
	
	@Inject
	private MamTipoItemEvolucaoDAO mamTipoItemEvolucaoDAO;

    @Inject
    private MamAnamnesesDAO mamAnamnesesDAO;

    @Inject
    private MamRegistroDAO mamRegistroDAO;

    @Inject
    private AghAtendimentoDAO aghAtendimentoDao;

    @Inject
    private MamExtratoRegistroDAO mamExtratoRegistroDAO;


    @Inject
	private MamTipoItemAnamnesesDAO mamTipoItemAnamnesesDAO;
	
	private static final long serialVersionUID = -6376763208866265609L;

	public String getDescricaoItemAnamnese() throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		CseCategoriaProfissional catProf = getCascaFacade()
				.primeiraCategoriaProfissional(servidorLogado);
		if (catProf == null) {
			return "";
		}
		List<MamTipoItemAnamneses> list = getMamTipoItemAnamnesesDAO()
				.buscaTipoItemAnamnesePorCategoriaOrdenado(catProf.getSeq());
		StringBuffer descricao = new StringBuffer();
		for (MamTipoItemAnamneses tipo : list) {
			descricao.append(tipo.getDescricao())
					.append(LINE_SEPARATOR)
					.append(LINE_SEPARATOR);
		}
		return descricao.toString();
	}
	
	public String getDescricaoItemAnamnese(RapServidores servidor) throws ApplicationBusinessException {
		return getDescricaoItemAnamnese();
	}

	public String getDescricaoItemEvolucao(RapServidores servidorLogado) throws ApplicationBusinessException {
		return getDescricaoItemEvolucao();
	}

	
	public String getDescricaoItemEvolucao() throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		CseCategoriaProfissional catProf = getCascaFacade()
				.primeiraCategoriaProfissional(servidorLogado);
		if (catProf == null) {
			return "";
		}
		List<MamTipoItemEvolucao> list = getMamTipoItemEvolucaoDAO()
				.buscaTipoItemEvolucaoPorCategoriaOrdenado(catProf.getSeq());
		StringBuffer descricao = new StringBuffer();
		for (MamTipoItemEvolucao tipo : list) {
			descricao.append(tipo.getDescricao())
					.append(LINE_SEPARATOR)
					.append(LINE_SEPARATOR);
		}
		return descricao.toString();
	}

    /**
     *
     * Nova rotina de geração do registro de atendimento (rgt) - versao 2
     *
     * PROCEDURE MAMP_INT_GERA_REG_2
     *
     * @param atdSeq
     * @param pIndPesqPend
     * @param pTipoPendPesq
     * @param pIndPedeSituacao
     * @param pSituacaoGerar
     * @return P_RGT_SEQ
     */
    public Long geraRegistroDeAtendimentoVersao2(Integer atdSeq,
                                                 String pIndPesqPend,
                                                 String pTipoPendPesq,
                                                 String pIndPedeSituacao,
                                                 DominioSituacaoRegistro pSituacaoGerar) throws ApplicationBusinessException {
        Long vRgtSeqRetorno = null;

        // busca se deve ou não permitir a pendencia de anamnese na internação
        AghParametros parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PERMITE_PEND_ANA_INT);
        String vPermitePendAnaInt = null;
        if (parametro.getVlrTexto() == null) {
            vPermitePendAnaInt="N";
        }else{
            vPermitePendAnaInt=parametro.getVlrTexto();
        }

        DominioSituacaoRegistro vSituacaoAGerar = null;
        if (pIndPedeSituacao != null  && pIndPedeSituacao.equalsIgnoreCase("S") && pSituacaoGerar != null) {
               vSituacaoAGerar = pSituacaoGerar;
        }else{
            vSituacaoAGerar = DominioSituacaoRegistro.VA;
        }

        Boolean vGeraRegistro = Boolean.FALSE;

        if (vPermitePendAnaInt != null && vPermitePendAnaInt.equalsIgnoreCase("S")
                    && pIndPedeSituacao !=null && pIndPedeSituacao.equalsIgnoreCase("S")
                        && pTipoPendPesq != null  && pTipoPendPesq.equalsIgnoreCase("ANA")) {


                //v_ser_vin_codigo := agh_config.get_vinculo;
                //v_ser_matricula  := agh_config.get_matricula;
                RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();


                //busca em quantas horas se pode recuperar uma anamnese de internação pendente
                AghParametros pHorasRecuperarAnamnsese = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HR_REC_ANA_INT_PEND);
                Long vHrRecAnaIntPend = Long.valueOf("24");
                if (pHorasRecuperarAnamnsese.getVlrNumerico() != null ) {
                    vHrRecAnaIntPend = Long.valueOf(pHorasRecuperarAnamnsese.getVlrNumerico().toString());
                }

                //OPEN cur_pend_ana_int (p_atd_seq, v_ser_matricula, v_ser_vin_codigo, v_hr_rec_ana_int_pend);
                //FETCH cur_pend_ana_int INTO v_rgt_seq_retorno, v_ind_situacao_pesq, v_ind_pendente_pesq;
                List<MamAnamneses> pendencias = mamAnamnesesDAO.buscaPendenciaAnamnesePorAtendimento(atdSeq,
                                                                    servidorLogado.getMatriculaVinculo(),
                                                                    servidorLogado.getCodigoVinculoNomeServidor(),
                                                                    vHrRecAnaIntPend);
                //IF cur_pend_ana_int%notfound
                if (pendencias == null  || pendencias.size() == 0) {
                    vGeraRegistro = Boolean.TRUE;
                }else {

                    for (MamAnamneses anamnesePendente : pendencias) {

                        if (anamnesePendente.getRegistro().getIndSituacao().equals(DominioSituacaoRegistro.PE)
                                && anamnesePendente.getPendente().equals(DominioIndPendenteAmbulatorio.P)) {

                            vGeraRegistro = Boolean.FALSE;

                            //-- neste ponto houve a recuperação da anamnese pendente (registro do atendimento
                            //-- também) para tanto é necessário em "em execução" o registro (isto significa dizer que
                            //-- foi retomado o registro do atendimento do paciente na internação após uma interupção).

                            //mamk_int_generica.mamp_int_atlz_reg (v_rgt_seq_retorno, 'EE');
                            vRgtSeqRetorno = intAtlzReg(anamnesePendente);

                        } else {
                            vGeraRegistro = Boolean.TRUE;
                        }
                    }

                }

        } else {
            vGeraRegistro = Boolean.TRUE;
        }

        if (vGeraRegistro) {
            Boolean vIndNoConsultorio = Boolean.FALSE;
            AghAtendimentos atendimento =   aghAtendimentoDao.obterAtendimentoOriginal(atdSeq);
            AghEspecialidades vEspSec = atendimento.getEspecialidade(); //mamk_int_generica.mamc_int_get_esp(p_atd_seq);
            AghUnidadesFuncionais vUnfSec = atendimento.getUnidadeFuncional(); // mamk_int_generica.mamc_int_get_unf(p_atd_seq);

            MamRegistro registro = new MamRegistro();
            registro.setIndNoConsultorio(vIndNoConsultorio);
            registro.setEspecialidade(vEspSec);
            registro.setUnidadeFuncional(vUnfSec);
            registro.setIndSituacao(vSituacaoAGerar);
            registro.setTipoFormularioAlta(DominioTipoFormularioAlta.I);
            registro.setIndPedeMotivo(Boolean.FALSE);
            registro.setAtendimento(atendimento);
            registro.setCriadoEm(new Date());
            registro.setServidor(servidorLogadoFacade.obterServidorLogado());
            mamRegistroDAO.persistir(registro);


            MamExtratoRegistro mamExtratoRegistro = new MamExtratoRegistro();
            mamExtratoRegistro.setIndSituacao(vSituacaoAGerar);
            MamExtratoRegistroId id = new MamExtratoRegistroId();
            id.setRgtSeq(registro.getSeq());
            //No AGHWEB está fixo o valor "1" para o campo 'seqP'
            id.setSeqp(Short.valueOf("1"));
            mamExtratoRegistro.setId(id);
            mamExtratoRegistro.setCriadoEm(new Date());
            mamExtratoRegistro.setServidor(servidorLogadoFacade.obterServidorLogado());
            mamExtratoRegistroDAO.persistir(mamExtratoRegistro);

            vRgtSeqRetorno = registro.getSeq();
        }



        return vRgtSeqRetorno;
    }

    /**
     * mamk_int_generica.mamp_int_atlz_reg (v_rgt_seq_retorno, 'EE');
     */
    private Long intAtlzReg(MamAnamneses anamnesePendente) {
        anamnesePendente.getRegistro().setIndSituacao(DominioSituacaoRegistro.EE);
        mamRegistroDAO.atualizar(anamnesePendente.getRegistro());

        MamExtratoRegistro mamExtratoRegistro = new MamExtratoRegistro();
        mamExtratoRegistro.setIndSituacao(DominioSituacaoRegistro.EE);
        MamExtratoRegistroId id = new MamExtratoRegistroId();

        id.setRgtSeq(anamnesePendente.getRegistro().getSeq());
        mamExtratoRegistro.setId(id);
        mamExtratoRegistroDAO.persistir(mamExtratoRegistro);
        return  anamnesePendente.getRegistro().getSeq();
    }

    protected ICascaFacade getCascaFacade(){
		return this.cascaFacade;
	}
	
	protected MamTipoItemEvolucaoDAO getMamTipoItemEvolucaoDAO(){
		return mamTipoItemEvolucaoDAO;
	}
	
	protected MamTipoItemAnamnesesDAO getMamTipoItemAnamnesesDAO() {
		return mamTipoItemAnamnesesDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
