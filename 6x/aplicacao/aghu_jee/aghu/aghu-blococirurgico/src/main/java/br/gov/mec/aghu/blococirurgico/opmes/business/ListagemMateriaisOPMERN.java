package br.gov.mec.aghu.blococirurgico.opmes.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcItensRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMateriaisItemOpmesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.vo.DemoFinanceiroOPMEVO;
import br.gov.mec.aghu.blococirurgico.vo.GrupoExcludenteVO;
import br.gov.mec.aghu.blococirurgico.vo.ItemProcedimentoHospitalarMaterialVO;
import br.gov.mec.aghu.blococirurgico.vo.ItemProcedimentoVO;
import br.gov.mec.aghu.blococirurgico.vo.MarcaComercialMaterialVO;
import br.gov.mec.aghu.blococirurgico.vo.MaterialHospitalarVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcOpmesListaGrupoExcludente;
import br.gov.mec.aghu.blococirurgico.vo.MbcOpmesVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioRequeridoItemRequisicao;
import br.gov.mec.aghu.dominio.DominioSituacaoMaterialItem;
import br.gov.mec.aghu.dominio.DominioTabelaGrupoExcludente;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.MbcItensRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcMateriaisItemOpmes;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.core.business.BaseBusiness;


@Stateless
public class ListagemMateriaisOPMERN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(ListagemMateriaisOPMERN.class);
	
	
	@Inject
	private MbcRequisicaoOpmesDAO mbcRequisicaoOpmesDAO;
	
	@Inject
	private MbcItensRequisicaoOpmesDAO mbcItensRequisicaoOpmesDAO;
	
	@Inject
	private MbcMateriaisItemOpmesDAO mbcMateriaisItemOpmesDAO;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private OPMEPortalAgendamentoRN oPMEPortalAgendamentoRN;
	
	@EJB 
	private OPMEPortalAgendamentoON oPMEPortalAgendamentoON;
	

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private static final long serialVersionUID = -4235271171014694459L;

	public MbcOpmesListaGrupoExcludente consultaItensProcedimento(Integer pciSeq,
			FatItensProcedHospitalar procedimentoSus, MbcRequisicaoOpmes requisicaoOpmes, Date dtAgenda) {

		requisicaoOpmes.getItensRequisicao().clear();

		List<ItemProcedimentoHospitalarMaterialVO> itens = mbcRequisicaoOpmesDAO.consultarItensProcedimentoHospitalarMateriais(pciSeq, procedimentoSus.getSeq(),procedimentoSus.getId().getPhoSeq());

		//filtrarMateriaisPreferencialCUM(itens);

		List<ItemProcedimentoHospitalarMaterialVO> itensMarcas = mbcItensRequisicaoOpmesDAO.recuperarMarcasItensProcedimentoHospitalarMateriais(itens,dtAgenda);

		List<MbcOpmesVO> opmes = new ArrayList<MbcOpmesVO>();
		List<MbcItensRequisicaoOpmes> itensRequisicoesOpmes = new ArrayList<MbcItensRequisicaoOpmes>();

		// Lista que armazena os grupos de excludentes
		List<GrupoExcludenteVO> listaGrupoExcludentes = new ArrayList<GrupoExcludenteVO>();
		Integer codigoGrupo = 1;
		for (ItemProcedimentoHospitalarMaterialVO itemProcedimentoHospitalarMaterialVO : itensMarcas) {
			itemProcedimentoHospitalarMaterialVO.setCmp(mbcRequisicaoOpmesDAO
					.getFatItensProcedHospitalar(itemProcedimentoHospitalarMaterialVO.getIphCompSeq(),
							itemProcedimentoHospitalarMaterialVO.getIphCompPho()));

			MbcItensRequisicaoOpmes itensRequisicaoOpmes = getOPMEPortalAgendamentoRN().obterItensRequisicao(
					itensRequisicoesOpmes, itemProcedimentoHospitalarMaterialVO, requisicaoOpmes);

			codigoGrupo = getCorExibicaoItem(itensRequisicaoOpmes, listaGrupoExcludentes, codigoGrupo);

			Short qtMaxima = itemProcedimentoHospitalarMaterialVO.getQtdMaxima();
			if (qtMaxima == null) {
				qtMaxima = itemProcedimentoHospitalarMaterialVO.getMaxQtdConta();
			}
			Integer qtdAutSus = itensRequisicaoOpmes.getQuantidadeAutorizadaSus() + qtMaxima;
			if (qtdAutSus != null) {
				itensRequisicaoOpmes.setQuantidadeAutorizadaSus(qtdAutSus.shortValue());
			}
			BigDecimal vlrUnitario = somarValoresUnitarios(itemProcedimentoHospitalarMaterialVO);
			itensRequisicaoOpmes.setValorUnitarioIph(itensRequisicaoOpmes.getValorUnitarioIph().add(vlrUnitario));
			MbcMateriaisItemOpmes materiaisItemOpmes = null;
			if (itemProcedimentoHospitalarMaterialVO.getMaterial() != null) {
				materiaisItemOpmes = new MbcMateriaisItemOpmes();
				materiaisItemOpmes.setSituacao(DominioSituacaoMaterialItem.A);
				materiaisItemOpmes.setItensRequisicaoOpmes(itensRequisicaoOpmes);
				if (itemProcedimentoHospitalarMaterialVO.getCodigoMat() != null) {
					materiaisItemOpmes.setProcedHospInternos(itemProcedimentoHospitalarMaterialVO.getCmpPhi());
					materiaisItemOpmes.setMaterial(itemProcedimentoHospitalarMaterialVO.getMaterial());
				}

				materiaisItemOpmes
						.setQuantidadeSolicitada(itemProcedimentoHospitalarMaterialVO.getMioQtdSolic() == null ? 0
								: itemProcedimentoHospitalarMaterialVO.getMioQtdSolic());
				itensRequisicaoOpmes.getMateriaisItemOpmes().add(materiaisItemOpmes);

				ScoMarcaComercial marcaMat = getIComprasFacade().obterMarcaComercialPorCodigo(itemProcedimentoHospitalarMaterialVO.getCodigoMarcasComerciais());
				materiaisItemOpmes.setScoMarcaComercial(marcaMat);
			}
			MbcOpmesVO vo = new MbcOpmesVO(itensRequisicaoOpmes, false,
					itemProcedimentoHospitalarMaterialVO.getIphCompCod(),
					itemProcedimentoHospitalarMaterialVO.getIphCompDscr(), materiaisItemOpmes);
			if (vo.getCodigoEDescricao() != null) {
				vo.setCodigoMarcasComerciais(itemProcedimentoHospitalarMaterialVO.getCodigoMarcasComerciais());
				vo.setDescricaoMarcasComerciais(itemProcedimentoHospitalarMaterialVO.getDescricaoMarcasComerciais());
				vo.setUnidadeMaterial(itemProcedimentoHospitalarMaterialVO.getUnidadeMaterial());
				vo.setValorUnitario(itemProcedimentoHospitalarMaterialVO.getValorUnitario());
			}
			opmes.add(vo);
		}
		
		

		montaQuebra(opmes, requisicaoOpmes, listaGrupoExcludentes);

		getOPMEPortalAgendamentoRN().atualizarRequisicaoCompatibilidade(requisicaoOpmes);

		for (MbcOpmesVO mbcOpmesVO : opmes) {
			oPMEPortalAgendamentoON.calculaQuantidadeAutorizada(mbcOpmesVO);
		}

		// agrupando itens por codTabela (IPH)
		Map<Long, List<MbcOpmesVO>> mapTabelaMateriais = new HashMap<Long, List<MbcOpmesVO>>();

		for (MbcOpmesVO mbcOpmesVO : opmes) {

			if (mbcOpmesVO.getItensRequisicaoOpmes() != null) {
				List<MbcOpmesVO> opmesPorCodTabela = mapTabelaMateriais.get(mbcOpmesVO.getCodTabela());
				if (opmesPorCodTabela == null) {
					opmesPorCodTabela = new ArrayList<MbcOpmesVO>();
				}
				definirCorGruposExcludencia(mbcOpmesVO, listaGrupoExcludentes);
				mapTabelaMateriais.put(mbcOpmesVO.getCodTabela(), opmesPorCodTabela);
				opmesPorCodTabela.add(mbcOpmesVO);
			}
		}
		opmes = filtrarOpmes(opmes);
		MbcOpmesListaGrupoExcludente voTransporte = new MbcOpmesListaGrupoExcludente();
		voTransporte.setListaPesquisada(opmes);
		voTransporte.setListaGrupoExcludente(listaGrupoExcludentes);
		voTransporte.setMapTabelaMateriais(mapTabelaMateriais);
		return voTransporte;

	}
	
	private List<MbcOpmesVO> filtrarOpmes(List<MbcOpmesVO>  opmes){
        List<MbcOpmesVO> listaSemRedundancia = new ArrayList<MbcOpmesVO>();
        for (MbcOpmesVO vo : opmes) {
            if (vo.getVoQuebra() == null) {
                //if (vo.getFilhos().size() > 1) {
                    List<MbcOpmesVO> listaFilhos = new ArrayList<MbcOpmesVO>();
                    for (MbcOpmesVO filhoVO : vo.getFilhos()) {
                        if (filhoVO.getUnidadeMaterial() != null) {
                            //vo.getFilhos().remove(filhoVO);
                            listaFilhos.add(filhoVO);
                            //break;
                        }
                    }
                    vo.setFilhos(null);
                    vo.setFilhos(listaFilhos);
                    List<MbcOpmesVO> filhos = new ArrayList<MbcOpmesVO>();
                    for (MbcOpmesVO filhoVO : vo.getFilhos()) {
                        if(!filhos.contains(filhoVO)){
                            filhos.add(filhoVO);
                        }
                    }
                    vo.setFilhos(filhos);
                //}
                if(vo.getFilhos() != null){
                    if(!vo.getFilhos().isEmpty()){
                        listaSemRedundancia.add(vo);
                    }
                }
                
            }
        }
        return listaSemRedundancia;
    }

//	private void filtrarMateriaisPreferencialCUM(List<ItemProcedimentoHospitalarMaterialVO> itens) {
//		for (ItemProcedimentoHospitalarMaterialVO itemProcedimentoHospitalarMaterialVO : itens) {
//			if (itemProcedimentoHospitalarMaterialVO.getCodigoMat() != null) {
//				ScoMaterial material = getIComprasFacade().getMaterialPreferencialCUM(itemProcedimentoHospitalarMaterialVO.getCodigoMat());
//				if (material == null) {
//					itemProcedimentoHospitalarMaterialVO.setCodigoMat(null);
//					itemProcedimentoHospitalarMaterialVO.setNomeMat(null);
//					itemProcedimentoHospitalarMaterialVO.setMaterial(null);
//				}
//			}
//		}
//
//		Collections.sort(itens);
//	}

	private void definirCorGruposExcludencia(MbcOpmesVO mbcOpmesVO, List<GrupoExcludenteVO> listaGrupoExcludentes) {
		for (GrupoExcludenteVO vo : listaGrupoExcludentes) {
			for (ItemProcedimentoVO item : vo.getListaGrupoExcludente()) {
				Integer seq = mbcOpmesVO.getItensRequisicaoOpmes().getFatItensProcedHospitalar().getId().getSeq();
				Short phoSeq = mbcOpmesVO.getItensRequisicaoOpmes().getFatItensProcedHospitalar().getId().getPhoSeq();
				if (item.getCmpSeq().equals(seq) && item.getCmpPhoSeq().equals(phoSeq)) {
					mbcOpmesVO.setCor(vo.getCor());
					break;
				}
			}
		}
	}

	/**
	 * Monta quebra dos vos para mostrar na GRID de forma visual Guardando
	 * referencias dos itens mais e filhos Soma quantidade de qtde solicitada
	 * com a quantidade de material
	 * 
	 * @param opmes
	 */
	private void montaQuebra(List<MbcOpmesVO> opmes, MbcRequisicaoOpmes requisicaoOpmes,
			List<GrupoExcludenteVO> listaGrupoExcludentes) {
		MbcItensRequisicaoOpmes itemAnterior = null;
		MbcOpmesVO voQuebra = null;

		List<MbcOpmesVO> opmesMaterialNulo = new ArrayList<MbcOpmesVO>();

		for (MbcOpmesVO mbcOpmesVO : opmes) {
			if (itemAnterior == null
					|| !mbcOpmesVO.getItensRequisicaoOpmes().getFatItensProcedHospitalar().getId()
							.equals(itemAnterior.getFatItensProcedHospitalar().getId())) {
				voQuebra = mbcOpmesVO;
				voQuebra.setFilhos(new ArrayList<MbcOpmesVO>());
				// adiciona filhos para calcular o valor total da quebra em tela
				voQuebra.getFilhos().add(voQuebra);
				// mbcOpmesVO.setVoQuebra(voQuebra);
				requisicaoOpmes.getItensRequisicao().add(mbcOpmesVO.getItensRequisicaoOpmes());
				mbcOpmesVO.setQtdeSol((mbcOpmesVO.getQtdeSol() + mbcOpmesVO.getQtdeSolicitadaMaterial()));
			} else {
				if (mbcOpmesVO.getMaterial() != null) {
					voQuebra.setQtdeSol((voQuebra.getQtdeSol() + mbcOpmesVO.getQtdeSolicitadaMaterial()));
					// adiciona filhos para calcular o valor total da quebra em
					// tela
					voQuebra.getFilhos().add(mbcOpmesVO);
					mbcOpmesVO.setVoQuebra(voQuebra);
					voQuebra.getItensRequisicaoOpmes().getMateriaisItemOpmes()
							.add(mbcOpmesVO.getMbcMateriaisItemOpmes());

				} else {
					opmesMaterialNulo.add(mbcOpmesVO);
				}
			}

			if (mbcOpmesVO.getVoQuebra() == null) {
				definirCorGruposExcludencia(mbcOpmesVO, listaGrupoExcludentes);
			}

			itemAnterior = mbcOpmesVO.getItensRequisicaoOpmes();
		}

		//opmes.removeAll(opmesMaterialNulo);
	}

	public MbcOpmesListaGrupoExcludente carregaGrid(MbcRequisicaoOpmes requisicaoOpmes) {
		//if (requisicaoOpmes != null) {
		//	requisicaoOpmes = mbcRequisicaoOpmesDAO.obterPorChavePrimaria(requisicaoOpmes.getSeq());
		//}

		// Lista que armazena os grupos de excludentes
		List<GrupoExcludenteVO> listaGrupoExcludentes = new ArrayList<GrupoExcludenteVO>();
		Integer codigoGrupo = 1;

		List<MbcOpmesVO> opmesVOs = transformToVos(requisicaoOpmes);

		for (MbcOpmesVO mbcOpmesVO : opmesVOs) {
			codigoGrupo = getCorExibicaoItem(mbcOpmesVO.getItensRequisicaoOpmes(), listaGrupoExcludentes, codigoGrupo);

			oPMEPortalAgendamentoON.calculaQuantidadeAutorizada(mbcOpmesVO);
			if (mbcOpmesVO.getVoQuebra() == null) {
				definirCorGruposExcludencia(mbcOpmesVO, listaGrupoExcludentes);
			}
		}

		oPMEPortalAgendamentoON.setCompatibilidadeGrupoExcludencia(opmesVOs);

		// regatorar, verificar se pode ser incluso no laço anterior sem
		// prejudicar regras
		for (MbcOpmesVO mbcOpmesVO : opmesVOs) {
			// carregando marca e unidade no objeto vo
			if (mbcOpmesVO.getMbcMateriaisItemOpmes() != null) {
				if (mbcOpmesVO.getMbcMateriaisItemOpmes().getScoMarcaComercial() != null) {
					mbcOpmesVO.setCodigoMarcasComerciais(mbcOpmesVO.getMbcMateriaisItemOpmes().getScoMarcaComercial().getCodigo());
					mbcOpmesVO.setDescricaoMarcasComerciais(mbcOpmesVO.getMbcMateriaisItemOpmes().getScoMarcaComercial().getDescricao());
				}
				if (mbcOpmesVO.getMbcMateriaisItemOpmes().getMaterial() != null) {
					mbcOpmesVO.setUnidadeMaterial(mbcOpmesVO.getMbcMateriaisItemOpmes().getMaterial().getUmdCodigo());
				}
			}
		}

		MbcOpmesListaGrupoExcludente voTransporte = new MbcOpmesListaGrupoExcludente();
		voTransporte.setListaPesquisada(opmesVOs);
		voTransporte.setListaGrupoExcludente(listaGrupoExcludentes);
		return voTransporte;

	}

	private List<MbcOpmesVO> transformToVos(MbcRequisicaoOpmes requisicaoOpmes) {
		List<MbcOpmesVO> opmesVOs = new ArrayList<MbcOpmesVO>();
		if (requisicaoOpmes != null) {

			List<MbcItensRequisicaoOpmes> itensOrdenadosNOV = new ArrayList<MbcItensRequisicaoOpmes>();
			List<MbcItensRequisicaoOpmes> itensOrdenadosADC = new ArrayList<MbcItensRequisicaoOpmes>();
			List<MbcItensRequisicaoOpmes> itensOrdenadosREQNRQ = new ArrayList<MbcItensRequisicaoOpmes>();
			List<MbcItensRequisicaoOpmes> itensOrdenados = new ArrayList<MbcItensRequisicaoOpmes>();

			for (MbcItensRequisicaoOpmes item : requisicaoOpmes.getItensRequisicao()) {
				if (DominioRequeridoItemRequisicao.NOV.equals(item.getRequerido())) {
					itensOrdenadosNOV.add(item);
				}
				if (DominioRequeridoItemRequisicao.ADC.equals(item.getRequerido())) {
					itensOrdenadosADC.add(item);
				}
				if (DominioRequeridoItemRequisicao.NRQ.equals(item.getRequerido())
						|| DominioRequeridoItemRequisicao.REQ.equals(item.getRequerido())) {
					itensOrdenadosREQNRQ.add(item);
				}
			}

			// ordena pelo codigo da tabela
			Collections.sort(itensOrdenadosREQNRQ, new Comparator<MbcItensRequisicaoOpmes>() {
				@Override
				public int compare(MbcItensRequisicaoOpmes c1, MbcItensRequisicaoOpmes c2) {
					return c1.getFatItensProcedHospitalar().getCodTabela()
							.compareTo(c2.getFatItensProcedHospitalar().getCodTabela());
				}
			});

			itensOrdenados.addAll(itensOrdenadosNOV);
			itensOrdenados.addAll(itensOrdenadosADC);
			itensOrdenados.addAll(itensOrdenadosREQNRQ);

			for (MbcItensRequisicaoOpmes item : itensOrdenados) {

				List<MbcMateriaisItemOpmes> listMateriais = new ArrayList<MbcMateriaisItemOpmes>(
						item.getMateriaisItemOpmes());

				// Ordena os materias
				Collections.sort(listMateriais, new Comparator<MbcMateriaisItemOpmes>() {
					@Override
					public int compare(MbcMateriaisItemOpmes c1, MbcMateriaisItemOpmes c2) {
						return c1.getMaterial().getCodigo().compareTo(c2.getMaterial().getCodigo());
					}
				});

				MbcMateriaisItemOpmes mbcMateriaisItemOpmes = null;
				if (listMateriais != null && !listMateriais.isEmpty()) {
					mbcMateriaisItemOpmes = listMateriais.get(0);
				}
				if(mbcMateriaisItemOpmes != null){
					mbcMateriaisItemOpmes = mbcMateriaisItemOpmesDAO.obterPorChavePrimaria(mbcMateriaisItemOpmes.getSeq());
					MbcOpmesVO vo = getOPMEPortalAgendamentoRN().adicionar(item, true, mbcMateriaisItemOpmes);
					List<MarcaComercialMaterialVO> marcasVo = mbcItensRequisicaoOpmesDAO.recuperaMarcasMaterial(mbcMateriaisItemOpmes.getMaterial().getCodigo(),requisicaoOpmes.getAgendas().getDtAgenda());

					vo = setarValorLicitado(marcasVo, mbcMateriaisItemOpmes, vo);
					vo.getFilhos().add(vo);
					opmesVOs.add(vo);
				
				
					int index = 0;
					List<ScoMarcaComercial> listaMarcas = new ArrayList<ScoMarcaComercial>();
					for (MbcMateriaisItemOpmes materialItemOpmes : item.getMateriaisItemOpmes()) {
						materialItemOpmes = mbcMateriaisItemOpmesDAO.obterPorChavePrimaria(materialItemOpmes.getSeq());
						if(!listaMarcas.contains(materialItemOpmes.getScoMarcaComercial())){
							listaMarcas.add(materialItemOpmes.getScoMarcaComercial());
							if (index > 0) {
								MbcOpmesVO voMaterial = getOPMEPortalAgendamentoRN().adicionar(item, true, materialItemOpmes);
								List<MarcaComercialMaterialVO> marcas = mbcItensRequisicaoOpmesDAO.recuperaMarcasMaterial(materialItemOpmes.getMaterial().getCodigo(),requisicaoOpmes.getAgendas().getDtAgenda());
								if(marcas != null && marcas.size()>0){
									for (MarcaComercialMaterialVO marcaComercialMaterialVO : marcas) {
										if(materialItemOpmes.getScoMarcaComercial().getCodigo().equals(marcaComercialMaterialVO.getCodigo())){
											voMaterial.setValorUnitario(marcaComercialMaterialVO.getValorUnitario());
											break;
										}
									}
								}
								voMaterial.setVoQuebra(vo);
								opmesVOs.add(voMaterial);
								vo.getFilhos().add(voMaterial);
							}
						}
						index++;
					}
				}
			}
		}
		return opmesVOs;
	}

	private MbcOpmesVO setarValorLicitado(List<MarcaComercialMaterialVO> marcasVo,MbcMateriaisItemOpmes mbcMateriaisItemOpmes,MbcOpmesVO vo){
		if(marcasVo != null && marcasVo.size()>0){
			//vo.setValorUnitario(marcasVo.get(0).getValorUnitario());
			for (MarcaComercialMaterialVO marcaComercialMaterialVO : marcasVo) {
				if(mbcMateriaisItemOpmes.getScoMarcaComercial().getCodigo().equals(marcaComercialMaterialVO.getCodigo())){
					vo.setValorUnitario(marcaComercialMaterialVO.getValorUnitario());
					break;
				}
			}
		}
		return vo;
	}

	private Integer getCorExibicaoItem(MbcItensRequisicaoOpmes itemRequisicao,
			List<GrupoExcludenteVO> listaGrupoExcludentes, Integer codigoGrupoInicial) {
		// #33880 - 4) Identificação dos Grupos de Excludências (Identificação
		// visual):
		Integer codigoGrupoAtual = codigoGrupoInicial;
		if (itemRequisicao.getFatItensProcedHospitalar() != null) {
			List<ItemProcedimentoVO> grupoExcludencia = mbcRequisicaoOpmesDAO
					.consultarExcludenciaMaterial(itemRequisicao.getFatItensProcedHospitalar().getId().getPhoSeq(),
							itemRequisicao.getFatItensProcedHospitalar().getId().getSeq());
			List<GrupoExcludenteVO> listaGruposAux = new ArrayList<GrupoExcludenteVO>();
			listaGruposAux.addAll(listaGrupoExcludentes);
			/**
			 * a) Se o material recuperado no passo 3 NÃO possui materiais
			 * “mutualmente exclusivos”, então o registro não deve ter
			 * tratamento de cores (não pertence a nenhum grupo).
			 */

			if (grupoExcludencia != null && !grupoExcludencia.isEmpty()) {
				/**
				 * b) Caso contrário (possui materiais “mutualmente
				 * exclusivos”): Verifica se ao menos um “material excludente”
				 * está no [RecordSet Grupos Excludentes]:
				 */
				if (listaGrupoExcludentes.isEmpty()) {
					codigoGrupoAtual = adicionarGrupo(listaGrupoExcludentes, grupoExcludencia, codigoGrupoInicial,
							itemRequisicao);
				} else {
					boolean achou = false;
					for (GrupoExcludenteVO gex : listaGruposAux) {
						for (ItemProcedimentoVO vo : grupoExcludencia) {
							achou = getExistenciaItemGrupoExcl(vo, gex);
							if (achou) {
								gex.getListaGrupoExcludente().addAll(grupoExcludencia);
								break;
							}
						}
						if (achou) {
							break;
						}
					}
					if (!achou) {
						codigoGrupoAtual = adicionarGrupo(listaGrupoExcludentes, grupoExcludencia, codigoGrupoAtual,
								itemRequisicao);
					}
				}
			}
		}

		return codigoGrupoAtual;
	}

	private boolean getExistenciaItemGrupoExcl(ItemProcedimentoVO item, GrupoExcludenteVO gex) {
		// Flag para verificar se o item já está dentro de um grupo que possui
		// cor.
		boolean achou = false;
		for (ItemProcedimentoVO itemGrupoVo : gex.getListaGrupoExcludente()) {
			if (item.getIpxPhoSeq().equals(itemGrupoVo.getIpxPhoSeq())
					&& item.getIpxSeq().equals(itemGrupoVo.getIpxSeq())) {
				achou = true;
			}
		}
		return achou;
	}

	private Integer adicionarGrupo(List<GrupoExcludenteVO> listaGrupoExcludentes,
			List<ItemProcedimentoVO> grupoExcludencia, Integer codigoGrupo, MbcItensRequisicaoOpmes itemRequisicao) {
		GrupoExcludenteVO ge = new GrupoExcludenteVO();
		ge.setCodigo(codigoGrupo);
		ge.setCor(DominioTabelaGrupoExcludente.getInstance(codigoGrupo));
		ge.setListaGrupoExcludente(grupoExcludencia);
		ge.setIphPhoSeq(itemRequisicao.getFatItensProcedHospitalar().getId().getPhoSeq());
		ge.setIphSeq(itemRequisicao.getFatItensProcedHospitalar().getId().getSeq());
		listaGrupoExcludentes.add(ge);
		Integer codigoGrupoAtual = codigoGrupo + 1;
		return codigoGrupoAtual;
	}

	private BigDecimal somarValoresUnitarios(ItemProcedimentoHospitalarMaterialVO itemProcedimentoHospitalarMaterialVO) {

		BigDecimal vlrAnestesia = BigDecimal.ZERO;
		if (itemProcedimentoHospitalarMaterialVO.getVlrAnestesia() != null) {
			vlrAnestesia = itemProcedimentoHospitalarMaterialVO.getVlrAnestesia();
		}
		BigDecimal vlrServHospitalar = BigDecimal.ZERO;
		if (itemProcedimentoHospitalarMaterialVO.getVlrServHospitalar() != null) {
			vlrServHospitalar = itemProcedimentoHospitalarMaterialVO.getVlrServHospitalar();
		}

		BigDecimal vlrServProfissional = itemProcedimentoHospitalarMaterialVO.getVlrServProfissional() == null ? BigDecimal.ZERO
				: itemProcedimentoHospitalarMaterialVO.getVlrServProfissional();// getValorServProfissional(itemProcedimentoHospitalarMaterialVO);

		BigDecimal vlrSadt = itemProcedimentoHospitalarMaterialVO.getVlrSadt() == null ? BigDecimal.ZERO
				: itemProcedimentoHospitalarMaterialVO.getVlrSadt();// getValorSadt(itemProcedimentoHospitalarMaterialVO);

		BigDecimal vlrProcedimento = itemProcedimentoHospitalarMaterialVO.getVlrProcedimento() == null ? BigDecimal.ZERO
				: itemProcedimentoHospitalarMaterialVO.getVlrProcedimento(); // getValorProcedimento(itemProcedimentoHospitalarMaterialVO);

		BigDecimal vlrUnitario = vlrAnestesia.add(vlrServHospitalar).add(vlrServProfissional).add(vlrSadt)
				.add(vlrProcedimento);
		return vlrUnitario;
	}

	private OPMEPortalAgendamentoRN getOPMEPortalAgendamentoRN() {

		return oPMEPortalAgendamentoRN;
	}
	
	private IComprasFacade getIComprasFacade(){
		return comprasFacade;
	}
	
	/**
	 * #37999
	 * Suggestion de material hospitalar
	 * 
	 */
	public List<MaterialHospitalarVO> pesquisarMaterialHospitalar(String matNome) {
		
		return mbcItensRequisicaoOpmesDAO.pesquisarMaterialHospitalar(matNome);
	}

	/**
	 * #37999
	 * Demonstrativo Financeiro de Uso de OPMEs
	 * 
	 */
	public List<DemoFinanceiroOPMEVO> pesquisaDemonstrativoFinanceiroOpmes(
			Date competenciaInicial, Date competenciaFinal, Short especialidadeSeq, Integer prontuario,
			Integer matCodigo)  {
		return mbcItensRequisicaoOpmesDAO.pesquisaDemonstrativoFinanceiroOpmes(competenciaInicial, competenciaFinal, especialidadeSeq, prontuario, matCodigo);
	}

}
