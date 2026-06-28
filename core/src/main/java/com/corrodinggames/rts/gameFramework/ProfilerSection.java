package com.corrodinggames.rts.gameFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.bs */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/bs.class */
public enum ProfilerSection {
    total { // from class: com.corrodinggames.rts.gameFramework.bs.1
    },
    update { // from class: com.corrodinggames.rts.gameFramework.bs.2
    },
    draw { // from class: com.corrodinggames.rts.gameFramework.bs.3
    },
    draw_game { // from class: com.corrodinggames.rts.gameFramework.bs.4
    },
    draw_end,
    draw_gui,
    draw_game_effects,
    update_game_shouldDraw,
    update_game_sortRender,
    update_do_all_collisions,
    update_do_all_collisions2,
    update_all_team_and_ai,
    update_geo_indexes,
    update_minimap,
    update_groupcontroller,
    draw_game_unit,
    draw_setup,
    draw_setup_fill,
    draw_setup_clip,
    draw_setup_drawMap,
    surface_draw,
    realdraw_in_drawthread,
    update_waiting_on_draw,
    draw_waiting_on_update,
    load_total,
    load_map,
    load_units,
    load_compression,
    init_total,
    init_unitcolour
}
