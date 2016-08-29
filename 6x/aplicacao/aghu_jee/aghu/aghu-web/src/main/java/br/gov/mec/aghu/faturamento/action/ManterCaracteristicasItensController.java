package br.gov.mec.aghu.faturamento.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatCaractItemProcHospId;
import br.gov.mec.aghu.model.FatGrupo;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatTipoCaractItens;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterCaracteristicasItensController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ManterCaracteristicasItensController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -3617727563243410698L;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IParametroFacade parametroFacade;

	private FatItensProcedHospitalar itemProcedHospClone;

	private FatItensProcedHospitalar itemProcedHosp;

	private List<FatCaractItemProcHosp> listaCaractItemProcHosp;

	private List<FatCaractItemProcHosp> listaCaractItemProcHospClone;

	private FatTipoCaractItens tipoCaracteristica;

	private Integer valorNumerico;

	private String valorChar;

	private Date valorData;

	private Integer seq;

	private Short phoSeq;

	private boolean ativo;

	// Usados na remocao de caracteristica
	private Integer tctSeq;

	public enum ManterCaracteristicasItensControllerExceptionCode implements BusinessExceptionCode {
		TIPO_CARACTERISTICA_JA_ASSOCIADO, INFORMAR_AO_MENOS_UM_VALOR, INFORMAR_SOMENTE_VALOR_CHAR, INFORMAR_PAB_MDC_EST_ALC, INFORMAR_S_C_A, ITEM_PROCED_HOSPITALAR_ALTERADO_SUCESSO, INFORMAR_SOMENTE_UM_VALOR, NENHUM_REGISTRO_ENCONTRADO_PRODUCAO_PHI;
	}

	public void inicio() {
        FatItensProcedHospitalarId id = new FatItensProcedHospitalarId();
        id.setPhoSeq(this.phoSeq);
        id.setSeq(this.seq);
        this.itemProcedHosp = this.faturamentoFacade.obterItemProcedHospitalarPorChavePrimaria(id);

        this.listaCaractItemProcHosp = (List<FatCaractItemProcHosp>) this.faturamentoFacade.listarCaractItemProcHospPorSeqEPhoSeq(
                this.phoSeq, this.seq);
        if (listaCaractItemProcHosp != null) {
            listaCaractItemProcHospClone = (List<FatCaractItemProcHosp>) ((ArrayList<FatCaractItemProcHosp>) listaCaractItemProcHosp)
                    .clone();
        }

        // Clonando iph
        try {
            this.itemProcedHospClone = this.faturamentoFacade.clonarItemProcedimentoHospitalar(this.itemProcedHosp);
        } catch (Exception e) {
            LOG.error("Exceção capturada: ", e);
            apresentarExcecaoNegocio(new BaseException(
                    ManterCaracteristicasItensControllerExceptionCode.NENHUM_REGISTRO_ENCONTRADO_PRODUCAO_PHI, e));
        }

        if (this.itemProcedHosp != null && DominioSituacao.A.equals(this.itemProcedHosp.getSituacao())) {
            this.ativo = true;
        } else {
            this.ativo = false;
        }
	}

	public void removerCaracteristica(FatCaractItemProcHosp item) {
		for (Iterator<FatCaractItemProcHosp> i = listaCaractItemProcHosp.iterator(); i.hasNext();) {
			FatCaractItemProcHosp elemento = (FatCaractItemProcHosp) i.next();
			if (elemento.getId().getTctSeq().equals(item.getId().getTctSeq())) {
				i.remove();
			}
		}
	}

	public void adicionarCaracteristica() throws ApplicationBusinessException, ApplicationBusinessException {
		boolean existe = false;
		try {
			this.validarCamposCaracteristica();

			if (listaCaractItemProcHosp != null && !listaCaractItemProcHosp.isEmpty()) {
				for (FatCaractItemProcHosp element : listaCaractItemProcHosp) {
					if (element.getTipoCaracteristicaItem().getSeq().equals(tipoCaracteristica.getSeq())) {
						existe = true;
						break;
					}
				}
			}
			if (!existe) {
				FatCaractItemProcHospId id = new FatCaractItemProcHospId();
				id.setIphPhoSeq(this.phoSeq);
				id.setIphSeq(this.seq);
				id.setTctSeq(this.tipoCaracteristica.getSeq());
				FatCaractItemProcHosp caractItem = new FatCaractItemProcHosp();
				caractItem.setId(id);

				caractItem.setItemProcedimentoHospitalar(this.itemProcedHosp);

				caractItem.setTipoCaracteristicaItem(this.tipoCaracteristica);
				caractItem.setValorNumerico(this.valorNumerico);
				caractItem.setValorChar(this.valorChar);
				caractItem.setValorData(this.valorData);

				listaCaractItemProcHosp.add(caractItem);// Adiciona na lista.

				// Limpa os campos apos adicionar na lista
				this.tipoCaracteristica = null;
				this.valorNumerico = null;
				this.valorChar = null;
				this.valorData = null;
			} else {
				throw new ApplicationBusinessException(ManterCaracteristicasItensControllerExceptionCode.TIPO_CARACTERISTICA_JA_ASSOCIADO);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private void validarCamposCaracteristica() throws ApplicationBusinessException, ApplicationBusinessException {
		if (StringUtils.isBlank(this.valorChar) && this.valorData == null && this.valorNumerico == null) {
			throw new ApplicationBusinessException(ManterCaracteristicasItensControllerExceptionCode.INFORMAR_AO_MENOS_UM_VALOR);
		} else if ((StringUtils.isNotBlank(this.valorChar) && (this.valorData != null || this.valorNumerico != null))
				|| (this.valorData != null && (StringUtils.isNotBlank(this.valorChar) || this.valorNumerico != null))
				|| (this.valorNumerico != null && (StringUtils.isNotBlank(this.valorChar) || this.valorData != null))) {
			throw new ApplicationBusinessException(ManterCaracteristicasItensControllerExceptionCode.INFORMAR_SOMENTE_UM_VALOR);

		} else {
			AghParametros parametroComplexidade = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_TIPO_CARAC_ITEM_PROCED_HOSP_COMPLEX);
			if (this.tipoCaracteristica.getSeq().equals(parametroComplexidade.getVlrNumerico().intValue())) {
				if (this.valorData != null || this.valorNumerico != null) {
					throw new ApplicationBusinessException(ManterCaracteristicasItensControllerExceptionCode.INFORMAR_SOMENTE_VALOR_CHAR);
				} else {
					AghParametros parametroValorChar1 = parametroFacade
							.buscarAghParametro(AghuParametrosEnum.P_VALORES_CHAR_PAB_MDC_EST_ALC);
					String parametro1 = parametroValorChar1.getVlrTexto();
					String[] parametros1 = StringUtils.split(parametro1, ",");
					boolean existe = false;
					for (String valor : parametros1) {
						if (valor.trim().equalsIgnoreCase(this.valorChar.trim())) {
							existe = true;
							break;
						}
					}
					if (!existe) {
						throw new ApplicationBusinessException(ManterCaracteristicasItensControllerExceptionCode.INFORMAR_PAB_MDC_EST_ALC);
					}
				}
			} else {
				AghParametros parametroEspApac = parametroFacade
						.buscarAghParametro(AghuParametrosEnum.P_TIPO_CARAC_ITEM_PROCED_HOSP_ESP_APAC);
				if (this.tipoCaracteristica.getSeq().equals(parametroEspApac.getVlrNumerico().intValue())) {
					if (this.valorData != null || this.valorNumerico != null) {
						throw new ApplicationBusinessException(
								ManterCaracteristicasItensControllerExceptionCode.INFORMAR_SOMENTE_VALOR_CHAR);
					} else {
						AghParametros parametroValorChar2 = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_VALORES_CHAR_S_C_A);
						String parametro2 = parametroValorChar2.getVlrTexto();
						String[] parametros2 = StringUtils.split(parametro2, ",");
						boolean existe = false;
						for (String valor : parametros2) {
							if (valor.trim().equalsIgnoreCase(this.valorChar.trim())) {
								existe = true;
								break;
							}
						}
						if (!existe) {
							throw new ApplicationBusinessException(ManterCaracteristicasItensControllerExceptionCode.INFORMAR_S_C_A);
						}
					}
				}
			}
		}
	}

	public String gravar() {
		try {
			if (this.ativo) {
				this.itemProcedHosp.setSituacao(DominioSituacao.A);
			} else {
				this.itemProcedHosp.setSituacao(DominioSituacao.I);
			}

			// Primeiro remove
			for (FatCaractItemProcHosp ciphClone : this.listaCaractItemProcHospClone) {
				if (!this.listaCaractItemProcHosp.contains(ciphClone)) {
					// REMOVER
					this.faturamentoFacade.removerCaractItemProcedimentoHospitalar(ciphClone);
				}
			}
			// Ordenando a lista antes de inserir para reproduzir o
			// comportamento do AGH (sequencia de mensagens de validacao, caso
			// ocorram).
			if (this.listaCaractItemProcHosp != null && !this.listaCaractItemProcHosp.isEmpty()) {
				final BeanComparator sorter = new BeanComparator(FatCaractItemProcHosp.Fields.TIPO_CARACTERISTICA_ITEM.toString() + "."
						+ FatTipoCaractItens.Fields.SEQ.toString(), new NullComparator(false));
				Collections.sort(this.listaCaractItemProcHosp, sorter);
			}

			for (FatCaractItemProcHosp ciph : this.listaCaractItemProcHosp) {
				if (!this.listaCaractItemProcHospClone.contains(ciph)) {
					// ADICIONAR
					this.faturamentoFacade.persistirCaractItemProcedimentoHospitalar(ciph, new Date());
				}
			}

			this.itemProcedHosp.setCaracteristicasItemProcHosp(new HashSet<FatCaractItemProcHosp>(this.listaCaractItemProcHosp));

			this.faturamentoFacade.persistirItemProcedimentoHospitalarComFlush(itemProcedHosp, itemProcedHospClone);

			apresentarMsgNegocio(Severity.INFO,
					ManterCaracteristicasItensControllerExceptionCode.ITEM_PROCED_HOSPITALAR_ALTERADO_SUCESSO.toString());

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return "";
		}
		return "manterItemPrincipal";
	}

	public List<FatTipoCaractItens> listarTiposCaracteristicasParaItens(String objPesquisa) {
		return this.returnSGWithCount(this.faturamentoFacade.listarTiposCaracteristicasParaItens(objPesquisa),listarTiposCaracteristicasParaItensCount(objPesquisa));
	}

	public Long listarTiposCaracteristicasParaItensCount(String objPesquisa) {
		return this.faturamentoFacade.listarTiposCaracteristicasParaItensCount(objPesquisa);
	}

	public List<FatGrupo> listarGruposPorCodigoOuDescricao(Object objPesquisa) {
		return this.faturamentoFacade.listarGruposPorCodigoOuDescricao(objPesquisa);
	}

	public Long listarGruposPorCodigoOuDescricaoCount(Object objPesquisa) {
		return this.faturamentoFacade.listarGruposPorCodigoOuDescricaoCount(objPesquisa);
	}

	public String cancelar() {
		return "manterItemPrincipal";
	}

	// ############################
	// ### GETTERs and SETTERs ####
	// ############################

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Short getPhoSeq() {
		return phoSeq;
	}

	public void setPhoSeq(Short phoSeq) {
		this.phoSeq = phoSeq;
	}

	public List<FatCaractItemProcHosp> getListaCaractItemProcHosp() {
		return listaCaractItemProcHosp;
	}

	public void setListaCaractItemProcHosp(List<FatCaractItemProcHosp> listaCaractItemProcHosp) {
		this.listaCaractItemProcHosp = listaCaractItemProcHosp;
	}

	public FatItensProcedHospitalar getItemProcedHospClone() {
		return itemProcedHospClone;
	}

	public void setItemProcedHospClone(FatItensProcedHospitalar itemProcedHospClone) {
		this.itemProcedHospClone = itemProcedHospClone;
	}

	public List<FatCaractItemProcHosp> getListaCaractItemProcHospClone() {
		return listaCaractItemProcHospClone;
	}

	public void setListaCaractItemProcHospClone(List<FatCaractItemProcHosp> listaCaractItemProcHospClone) {
		this.listaCaractItemProcHospClone = listaCaractItemProcHospClone;
	}

	public FatTipoCaractItens getTipoCaracteristica() {
		return tipoCaracteristica;
	}

	public void setTipoCaracteristica(FatTipoCaractItens tipoCaracteristica) {
		this.tipoCaracteristica = tipoCaracteristica;
	}

	public Integer getValorNumerico() {
		return valorNumerico;
	}

	public void setValorNumerico(Integer valorNumerico) {
		this.valorNumerico = valorNumerico;
	}

	public String getValorChar() {
		return valorChar;
	}

	public void setValorChar(String valorChar) {
		this.valorChar = valorChar;
	}

	public Date getValorData() {
		return valorData;
	}

	public void setValorData(Date valorData) {
		this.valorData = valorData;
	}

	public FatItensProcedHospitalar getItemProcedHosp() {
		return itemProcedHosp;
	}

	public void setItemProcedHosp(FatItensProcedHospitalar itemProcedHosp) {
		this.itemProcedHosp = itemProcedHosp;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Integer getTctSeq() {
		return tctSeq;
	}

	public void setTctSeq(Integer tctSeq) {
		this.tctSeq = tctSeq;
	}

}